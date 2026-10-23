[CmdletBinding()]
param(
    [string]$AssetsRoot = 'C:\Proyectos\Zombie-Lane-Assets',
    [string]$OutputRoot = '',
    [string]$Version = '0.1.2',
    [string]$JavacPath = '',
    [switch]$Release,
    [switch]$SkipZip,
    [switch]$SkipSmoke
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$script:AllowedTopLevelNames = @(
    'runtime',
    'lib',
    'game-assets-ios',
    'client-overrides',
    'Run Zombie Lane Alpha.bat',
    'VERSION.txt',
    'RELEASE-NOTES.txt'
)
$script:ForbiddenDirectoryNames = @(
    '.git', '.svn', '.hg',
    'src', 'src-dev', 'src-release',
    'tools', 'tests', 'docs', 'packaging',
    'server', 'server-data',
    'build', 'out', '.github',
    'node_modules', '__pycache__'
)
$script:JunkFileNames = @(
    'accounts.properties',
    'mailbox.properties',
    'launcher.properties',
    'alpha.properties',
    'sesion-en-curso.properties',
    'session-in-progress.properties',
    'Thumbs.db',
    'desktop.ini',
    '.DS_Store'
)
$script:AssetExcludedNames = @(
    '.git', '.svn', '.hg',
    'backup', 'Thumbs.db', 'desktop.ini', '.DS_Store',
    '__pycache__', 'node_modules', 'src.zip'
)

function Get-NormalizedPath {
    param([Parameter(Mandatory = $true)][string]$Path)
    $full = [System.IO.Path]::GetFullPath($Path)
    $root = [System.IO.Path]::GetPathRoot($full)
    if ($full.Length -le $root.Length) {
        return $root
    }
    return $full.TrimEnd([System.IO.Path]::DirectorySeparatorChar, [System.IO.Path]::AltDirectorySeparatorChar)
}

function Test-SamePath {
    param(
        [Parameter(Mandatory = $true)][string]$Path,
        [Parameter(Mandatory = $true)][string]$Other
    )
    return [string]::Equals((Get-NormalizedPath $Path), (Get-NormalizedPath $Other), [System.StringComparison]::OrdinalIgnoreCase)
}

function Test-PathWithin {
    param(
        [Parameter(Mandatory = $true)][string]$Path,
        [Parameter(Mandatory = $true)][string]$Parent
    )
    $normalizedPath = Get-NormalizedPath $Path
    $normalizedParent = Get-NormalizedPath $Parent
    if (Test-SamePath $normalizedPath $normalizedParent) {
        return $false
    }
    $pathWithSeparator = $normalizedPath.TrimEnd('\', '/') + [System.IO.Path]::DirectorySeparatorChar
    $parentWithSeparator = $normalizedParent.TrimEnd('\', '/') + [System.IO.Path]::DirectorySeparatorChar
    return $pathWithSeparator.StartsWith($parentWithSeparator, [System.StringComparison]::OrdinalIgnoreCase)
}

function Get-RelativePath {
    param(
        [Parameter(Mandatory = $true)][string]$Root,
        [Parameter(Mandatory = $true)][string]$FullName
    )
    return $FullName.Substring($Root.Length).TrimStart('\', '/').Replace('/', '\')
}

function Invoke-Native {
    param(
        [Parameter(Mandatory = $true)][string]$FilePath,
        [string[]]$Arguments = @()
    )
    $previousErrorActionPreference = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        $output = @(& $FilePath @Arguments 2>&1)
        $exitCode = $LASTEXITCODE
    } finally {
        $ErrorActionPreference = $previousErrorActionPreference
    }
    return [pscustomobject]@{
        Output = @($output | ForEach-Object { $_.ToString() })
        ExitCode = $exitCode
    }
}

function Get-GitOutput {
    param(
        [Parameter(Mandatory = $true)][string]$RepositoryRoot,
        [Parameter(Mandatory = $true)][string[]]$Arguments
    )
    $result = Invoke-Native -FilePath 'git' -Arguments (@('-C', $RepositoryRoot) + $Arguments)
    if ($result.ExitCode -ne 0) {
        throw "git $($Arguments -join ' ') failed in '$RepositoryRoot': $($result.Output -join "`n")"
    }
    return $result.Output
}

function Get-JavacVersion {
    param([Parameter(Mandatory = $true)][string]$Javac)
    $result = Invoke-Native -FilePath $Javac -Arguments @('-version')
    if ($result.ExitCode -ne 0) {
        return $null
    }
    $text = ($result.Output -join "`n")
    $match = [regex]::Match($text, 'javac\s+(?<version>\d+(?:\.\d+)*)')
    if (-not $match.Success) {
        return $null
    }
    $rawVersion = $match.Groups['version'].Value
    $major = [int]($rawVersion.Split('.')[0])
    return [pscustomobject]@{
        Version = $rawVersion
        Major = $major
    }
}

function Get-JavaVersion {
    param([Parameter(Mandatory = $true)][string]$Java)
    $result = Invoke-Native -FilePath $Java -Arguments @('-version')
    if ($result.ExitCode -ne 0) {
        return $null
    }
    $text = ($result.Output -join "`n")
    $match = [regex]::Match($text, 'version\s+"(?<version>[^"]+)"')
    if (-not $match.Success) {
        return $null
    }
    $rawVersion = $match.Groups['version'].Value
    $majorMatch = [regex]::Match($rawVersion, '^(?<major>\d+)')
    if (-not $majorMatch.Success) {
        return $null
    }
    $major = [int]$majorMatch.Groups['major'].Value
    return [pscustomobject]@{
        Version = $rawVersion
        Major = $major
    }
}

function Resolve-Compiler {
    param(
        [Parameter(Mandatory = $true)][string]$AssetsRoot,
        [string]$JavacPath = ''
    )
    $candidates = New-Object 'System.Collections.Generic.List[object]'
    $seenPaths = New-Object 'System.Collections.Generic.HashSet[string]' ([System.StringComparer]::OrdinalIgnoreCase)

    if (-not [string]::IsNullOrWhiteSpace($JavacPath)) {
        $resolved = $JavacPath
        $command = Get-Command -Name $JavacPath -ErrorAction SilentlyContinue
        if ($null -ne $command -and -not [string]::IsNullOrWhiteSpace($command.Source)) {
            $resolved = $command.Source
        }
        if (-not (Test-Path -LiteralPath $resolved -PathType Leaf)) {
            throw "The requested javac executable was not found: $JavacPath"
        }
        $fullPath = Get-NormalizedPath $resolved
        $null = $seenPaths.Add($fullPath)
        $candidates.Add([pscustomobject]@{ Path = $fullPath; Explicit = $true })
    } else {
        $command = Get-Command -Name javac -ErrorAction SilentlyContinue
        if ($null -ne $command -and -not [string]::IsNullOrWhiteSpace($command.Source)) {
            $fullPath = Get-NormalizedPath $command.Source
            if ($seenPaths.Add($fullPath)) {
                $candidates.Add([pscustomobject]@{ Path = $fullPath; Explicit = $false })
            }
        }
        $bundledJavac = Join-Path $AssetsRoot 'runtime\bin\javac.exe'
        if (Test-Path -LiteralPath $bundledJavac -PathType Leaf) {
            $fullPath = Get-NormalizedPath $bundledJavac
            if ($seenPaths.Add($fullPath)) {
                $candidates.Add([pscustomobject]@{ Path = $fullPath; Explicit = $false })
            }
        }
    }

    if ($candidates.Count -eq 0) {
        throw 'No javac executable was found. Install a JDK 21 or newer, or pass -JavacPath.'
    }

    $rejected = New-Object 'System.Collections.Generic.List[string]'
    foreach ($candidate in $candidates) {
        $version = Get-JavacVersion -Javac $candidate.Path
        if ($null -eq $version) {
            $rejected.Add("$($candidate.Path): unknown version")
            continue
        }
        if ($version.Major -lt 21) {
            $rejected.Add("$($candidate.Path): $($version.Version)")
            if ($candidate.Explicit) {
                throw "The requested javac is older than Java 21: $($candidate.Path) reports $($version.Version)."
            }
            continue
        }
        $javapPath = Join-Path (Split-Path -Parent $candidate.Path) 'javap.exe'
        if (-not (Test-Path -LiteralPath $javapPath -PathType Leaf)) {
            $javapCommand = Get-Command -Name javap -ErrorAction SilentlyContinue
            if ($null -eq $javapCommand -or [string]::IsNullOrWhiteSpace($javapCommand.Source)) {
                throw "javap was not found next to javac and is not on PATH: $($candidate.Path)"
            }
            $javapPath = $javapCommand.Source
        }
        return [pscustomobject]@{
            Path = $candidate.Path
            Version = $version.Version
            JavapPath = Get-NormalizedPath $javapPath
        }
    }

    throw "No Java 21 or newer javac was found. Rejected candidates:`n$($rejected -join "`n")"
}

function Assert-Assets {
    param([Parameter(Mandatory = $true)][string]$AssetsRoot)
    if (-not (Test-Path -LiteralPath $AssetsRoot -PathType Container)) {
        throw "AssetsRoot does not exist: $AssetsRoot"
    }
    $java = Join-Path $AssetsRoot 'runtime\bin\java.exe'
    if (-not (Test-Path -LiteralPath $java -PathType Leaf)) {
        throw "The assets runtime is missing java.exe: $java"
    }
    $libDirectory = Join-Path $AssetsRoot 'lib'
    if (-not (Test-Path -LiteralPath $libDirectory -PathType Container)) {
        throw "The assets lib directory is missing: $libDirectory"
    }
    $jarCount = @(Get-ChildItem -LiteralPath $libDirectory -File -Force | Where-Object { $_.Extension -ieq '.jar' }).Count
    if ($jarCount -eq 0) {
        throw "The assets lib directory contains no JAR files: $libDirectory"
    }
    $gameAssets = Join-Path $AssetsRoot 'game-assets-ios'
    if (-not (Test-Path -LiteralPath $gameAssets -PathType Container)) {
        throw "The game asset bundle is missing: $gameAssets"
    }
    foreach ($requiredName in @('r1', 'l0_0', 'p', 'bundle_0')) {
        $requiredPath = Join-Path $gameAssets $requiredName
        if (-not (Test-Path -LiteralPath $requiredPath)) {
            throw "The game asset bundle is missing a required entry: $requiredPath"
        }
    }
    $backupDirectories = @(Get-ChildItem -LiteralPath $gameAssets -Directory -Force | Where-Object { $_.Name -match '(?i)backup' })
    foreach ($backupDirectory in $backupDirectories) {
        Write-Warning "Excluding backup directory from the release: $($backupDirectory.Name)"
    }
    return [pscustomobject]@{
        JavaPath = Get-NormalizedPath $java
        JarCount = $jarCount
    }
}

function Assert-RepositoryPreconditions {
    param([Parameter(Mandatory = $true)][string]$RepositoryRoot)
    if ($null -eq (Get-Command -Name git -ErrorAction SilentlyContinue)) {
        throw 'git is required to build a release.'
    }
    $insideWorkTree = (Get-GitOutput -RepositoryRoot $RepositoryRoot -Arguments @('rev-parse', '--is-inside-work-tree')) -join ''
    if ($insideWorkTree.Trim() -ne 'true') {
        throw "The repository directory is not a Git work tree: $RepositoryRoot"
    }
    $topLevel = (Get-GitOutput -RepositoryRoot $RepositoryRoot -Arguments @('rev-parse', '--show-toplevel')) -join ''
    if (-not (Test-SamePath $topLevel $RepositoryRoot)) {
        throw "The build script is not at the repository root: $RepositoryRoot"
    }
    $branch = (Get-GitOutput -RepositoryRoot $RepositoryRoot -Arguments @('branch', '--show-current')) -join ''
    if ([string]::IsNullOrWhiteSpace($branch)) {
        $branch = '(detached HEAD)'
    }
    $headFull = (Get-GitOutput -RepositoryRoot $RepositoryRoot -Arguments @('rev-parse', 'HEAD')) -join ''
    $headShort = (Get-GitOutput -RepositoryRoot $RepositoryRoot -Arguments @('rev-parse', '--short', 'HEAD')) -join ''

    $sourceStatus = New-Object 'System.Collections.Generic.List[string]'
    foreach ($line in @(Get-GitOutput -RepositoryRoot $RepositoryRoot -Arguments @(
        'status', '--porcelain=v1', '--untracked-files=all', '--',
        'src', 'src-release', 'tools/client-classes.txt', 'tools/build-client.ps1'
    ))) {
        $sourceStatus.Add($line)
    }
    foreach ($requiredTrackedPath in @(
        'tools/build-client.ps1',
        'tools/client-classes.txt',
        'src-release/zlport/dev/DevMode.java',
        'src-release/zlport/dev/LauncherDevOptions.java'
    )) {
        $tracked = @(Get-GitOutput -RepositoryRoot $RepositoryRoot -Arguments @('ls-files', '--', $requiredTrackedPath))
        if ($tracked.Count -eq 0) {
            $alreadyListed = @($sourceStatus | Where-Object {
                $_ -match ('^\?\?\s+' + [regex]::Escape($requiredTrackedPath) + '$')
            }).Count -gt 0
            if (-not $alreadyListed) {
                $sourceStatus.Add("?? $requiredTrackedPath (untracked)")
            }
        }
    }
    $sourceState = if ($sourceStatus.Count -eq 0) { 'clean' } else { 'dirty' }
    if ($Release -and $sourceState -ne 'clean') {
        throw "Release builds require clean, committed sources. Commit or revert these changes first:`n$($sourceStatus -join "`n")"
    }
    if (-not $Release -and $sourceState -ne 'clean') {
        Write-Warning 'The source tree has uncommitted changes. This build is a development build; use -Release for a public release.'
    }

    foreach ($forbiddenRepositoryPath in @('src-dev', 'src\zlport\dev', 'server', 'server-data')) {
        $fullPath = Join-Path $RepositoryRoot $forbiddenRepositoryPath
        if (Test-Path -LiteralPath $fullPath) {
            throw "The public client tree must not contain: $forbiddenRepositoryPath"
        }
    }
    foreach ($forbiddenTrackedPrefix in @('src-dev/', 'server/', 'server-data/', 'packaging/')) {
        $tracked = @(Get-GitOutput -RepositoryRoot $RepositoryRoot -Arguments @('ls-files', '--', $forbiddenTrackedPrefix))
        if ($tracked.Count -gt 0) {
            throw "The public client tree must not track files under: $forbiddenTrackedPrefix"
        }
    }
    foreach ($artifactPattern in @('*.class', '*.jar', '*.zip', '*.dll', '*.exe')) {
        $tracked = @(Get-GitOutput -RepositoryRoot $RepositoryRoot -Arguments @('ls-files', '--', $artifactPattern))
        if ($tracked.Count -gt 0) {
            throw "The public client tree must not track build artifacts ($artifactPattern):`n$($tracked -join "`n")"
        }
    }
    foreach ($ignoredDirectory in @('client-overrides', 'lib', 'runtime', 'game-assets-ios')) {
        $ignoreProbePath = $ignoredDirectory + '/probe'
        $ignoreResult = Invoke-Native -FilePath 'git' -Arguments @('-C', $RepositoryRoot, 'check-ignore', '-q', $ignoreProbePath)
        if ($ignoreResult.ExitCode -ne 0) {
            throw "$ignoredDirectory must be ignored by .gitignore."
        }
        $tracked = @(Get-GitOutput -RepositoryRoot $RepositoryRoot -Arguments @('ls-files', '--', $ignoredDirectory))
        if ($tracked.Count -gt 0) {
            throw "$ignoredDirectory must not be tracked by Git."
        }
    }

    $sourceRoot = Join-Path $RepositoryRoot 'src'
    if (-not (Test-Path -LiteralPath $sourceRoot -PathType Container)) {
        throw "The source directory is missing: $sourceRoot"
    }
    foreach ($releaseStub in @(
        'src-release\zlport\dev\DevMode.java',
        'src-release\zlport\dev\LauncherDevOptions.java'
    )) {
        $stubPath = Join-Path $RepositoryRoot $releaseStub
        if (-not (Test-Path -LiteralPath $stubPath -PathType Leaf)) {
            throw "The release stub is missing: $releaseStub"
        }
    }
    $sourceLeaks = @(Get-ChildItem -LiteralPath $sourceRoot -File -Recurse -Force | Where-Object { $_.Name -match '(?i)Smoke|Probe' })
    if ($sourceLeaks.Count -gt 0) {
        throw "The public source tree must not contain smoke or probe files:`n$($sourceLeaks.FullName -join "`n")"
    }

    $classListPath = Join-Path $RepositoryRoot 'tools\client-classes.txt'
    if (-not (Test-Path -LiteralPath $classListPath -PathType Leaf)) {
        throw "The client class list is missing: $classListPath"
    }
    $classList = @(
        Get-Content -LiteralPath $classListPath |
            ForEach-Object { $_.Trim() } |
            Where-Object { $_ -ne '' -and -not $_.StartsWith('#') }
    )
    if ($classList.Count -eq 0) {
        throw 'The client class list is empty.'
    }
    $seenEntries = New-Object 'System.Collections.Generic.HashSet[string]' ([System.StringComparer]::OrdinalIgnoreCase)
    foreach ($entry in $classList) {
        $normalizedEntry = $entry.Replace('/', '\')
        if ($normalizedEntry -notmatch '^src\\[^\\]+\\[^\\]+') {
            throw "Class list entries must be under src\<module>\: $entry"
        }
        if ($normalizedEntry -match '(?i)(^|\\)(src-dev|src-release)(\\|$)') {
            throw "Class list entries must not point at development or release-stub sources: $entry"
        }
        if ($normalizedEntry -match '(?i)(^|\\)(Smoke|Probe)(\\|$)') {
            throw "Class list entries must not be smoke or probe classes: $entry"
        }
        if (-not $seenEntries.Add($normalizedEntry)) {
            throw "Duplicate class list entry: $entry"
        }
        $fullSourcePath = Join-Path $RepositoryRoot $normalizedEntry
        if (-not (Test-Path -LiteralPath $fullSourcePath -PathType Leaf)) {
            throw "Class list source is missing: $entry"
        }
        if ([System.IO.Path]::GetExtension($fullSourcePath) -ine '.java') {
            throw "Class list entries must be Java sources: $entry"
        }
    }
    foreach ($requiredEntry in @(
        'src\launcher-ios\zlport\launcher\IosMain.java',
        'src\runtime-ios\zlport\ios\runtime\IosDriver.java'
    )) {
        if (-not ($classList | Where-Object { $_.Replace('/', '\') -ieq $requiredEntry })) {
            throw "The class list must include: $requiredEntry"
        }
    }

    return [pscustomobject]@{
        Branch = $branch
        HeadFull = $headFull
        HeadShort = $headShort
        SourceState = $sourceState
        SourceStatus = $sourceStatus.ToArray()
        ClassList = $classList
        ClassListPath = $classListPath
    }
}

function Assert-OutputRootSafe {
    param(
        [Parameter(Mandatory = $true)][string]$OutputRoot,
        [Parameter(Mandatory = $true)][string]$RepositoryRoot,
        [Parameter(Mandatory = $true)][string]$AssetsRoot
    )
    if (Test-SamePath $OutputRoot $RepositoryRoot) {
        throw 'OutputRoot must not be the repository root.'
    }
    if (Test-SamePath $OutputRoot $AssetsRoot) {
        throw 'OutputRoot must not be the assets root.'
    }
    if (Test-PathWithin -Path $OutputRoot -Parent $RepositoryRoot) {
        throw "OutputRoot must not be inside the repository: $OutputRoot"
    }
    if (Test-PathWithin -Path $RepositoryRoot -Parent $OutputRoot) {
        throw "OutputRoot must not contain the repository: $OutputRoot"
    }
    if (Test-PathWithin -Path $OutputRoot -Parent $AssetsRoot) {
        throw "OutputRoot must not be inside the assets root: $OutputRoot"
    }
    if (Test-PathWithin -Path $AssetsRoot -Parent $OutputRoot) {
        throw "OutputRoot must not contain the assets root: $OutputRoot"
    }
    if ((Get-NormalizedPath $OutputRoot) -match '^[A-Za-z]:\\?$') {
        throw 'Refusing to use a drive root as OutputRoot.'
    }
    if (Test-Path -LiteralPath $OutputRoot -PathType Leaf) {
        throw "OutputRoot is a file, not a directory: $OutputRoot"
    }
    if (Test-Path -LiteralPath $OutputRoot -PathType Container) {
        $leaf = Split-Path -Leaf $OutputRoot
        if ($leaf -notmatch '(?i)zombie.?lane') {
            throw "Refusing to delete an existing OutputRoot whose name does not contain 'Zombie Lane': $OutputRoot"
        }
        $gitDirectory = Join-Path $OutputRoot '.git'
        if (Test-Path -LiteralPath $gitDirectory) {
            $remotes = @(Get-GitOutput -RepositoryRoot $OutputRoot -Arguments @('remote'))
            foreach ($remoteName in $remotes) {
                $remoteUrl = @(Get-GitOutput -RepositoryRoot $OutputRoot -Arguments @('remote', 'get-url', $remoteName)) -join ''
                if (-not [string]::IsNullOrWhiteSpace($remoteUrl.Trim())) {
                    $safeRemote = $remoteUrl.Trim() -replace '(?<=://)[^/@\s]+@', '[credentials]@'
                    throw "Refusing to delete OutputRoot because its .git folder has a non-empty remote '$remoteName': $safeRemote"
                }
            }
        }
    }
}

function Copy-AssetTree {
    param(
        [Parameter(Mandatory = $true)][string]$Source,
        [Parameter(Mandatory = $true)][string]$Destination
    )
    [System.IO.Directory]::CreateDirectory($Destination) | Out-Null
    foreach ($item in Get-ChildItem -LiteralPath $Source -Force) {
        if ($item.Name -match '(?i)backup') {
            continue
        }
        $excluded = $false
        foreach ($excludedName in $script:AssetExcludedNames) {
            if ($item.Name -ieq $excludedName) {
                $excluded = $true
                break
            }
        }
        if ($excluded) {
            continue
        }
        if ($item.PSIsContainer) {
            Copy-AssetTree -Source $item.FullName -Destination (Join-Path $Destination $item.Name)
        } else {
            Copy-Item -LiteralPath $item.FullName -Destination (Join-Path $Destination $item.Name) -Force
        }
    }
}

function Test-ReleaseRelativePath {
    param(
        [Parameter(Mandatory = $true)][string]$RelativePath,
        [Parameter(Mandatory = $true)][bool]$IsDirectory
    )
    $relative = $RelativePath.Replace('/', '\')
    $segments = @($relative -split '\\' | Where-Object { $_ -ne '' })
    if ($segments.Count -eq 0) {
        return 'empty path'
    }
    foreach ($segment in $segments) {
        if ($segment -eq '.' -or $segment -eq '..') {
            return 'path traversal segment'
        }
        if ($segment -match '(?i)backup') {
            return "backup entry '$segment'"
        }
        if ($script:ForbiddenDirectoryNames -contains $segment) {
            $serverRuntimeExemption = $segment -ieq 'server' -and (
                $relative -ieq 'runtime\bin\server' -or $relative -like 'runtime\bin\server\*'
            )
            if (-not $serverRuntimeExemption) {
                return "forbidden directory '$segment'"
            }
        }
    }
    if ($IsDirectory) {
        return $null
    }

    $leaf = $segments[-1]
    if ($script:JunkFileNames -contains $leaf) {
        return "forbidden file '$leaf'"
    }
    if ($leaf -match '(?i)\.(java|ps1|cs|sql|jsonl|log)$') {
        return "forbidden source or log file '$leaf'"
    }
    if ($leaf -match '(?i)\.(zip|7z|rar|tar|gz|tgz|bz2|xz)$') {
        return "forbidden archive '$leaf'"
    }

    $extension = [System.IO.Path]::GetExtension($leaf).ToLowerInvariant()
    if ($extension -eq '.class') {
        if ($relative -notlike 'client-overrides\*') {
            return 'class file outside client-overrides'
        }
        $topLevelClassName = $leaf.Split('$')[0]
        if ($topLevelClassName -match '(?i)Smoke|Probe') {
            return 'smoke or probe class'
        }
    }
    if ($extension -eq '.md' -and $relative -notlike 'runtime\legal\*') {
        return 'markdown outside runtime legal'
    }
    if ($extension -eq '.properties' -and -not ($relative -like 'runtime\*' -or $relative -like 'game-assets-ios\*')) {
        return 'properties file outside runtime or game assets'
    }
    if ($extension -eq '.jar' -and -not ($relative -like 'lib\*' -or $relative -like 'runtime\lib\*')) {
        return 'jar outside lib or runtime/lib'
    }
    if ($extension -in @('.bat', '.cmd', '.sh', '.psm1', '.py') -and $relative -ine 'Run Zombie Lane Alpha.bat') {
        return 'script outside the launcher'
    }
    if ($extension -in @('.exe', '.dll', '.so', '.dylib') -and -not ($relative -like 'runtime\*' -or ($extension -ne '.exe' -and $relative -like 'lib\*'))) {
        return 'native binary outside runtime'
    }
    if ($extension -eq '.sqlite' -and $relative -notlike 'game-assets-ios\*') {
        return 'sqlite database outside game assets'
    }
    return $null
}

function Assert-NoForbiddenEntries {
    param([Parameter(Mandatory = $true)][string]$Root)
    $violations = New-Object 'System.Collections.Generic.List[string]'
    foreach ($item in Get-ChildItem -LiteralPath $Root -Force) {
        $allowed = $false
        foreach ($allowedName in $script:AllowedTopLevelNames) {
            if ($item.Name -ieq $allowedName) {
                $allowed = $true
                break
            }
        }
        if (-not $allowed) {
            $violations.Add("unexpected top-level entry: $($item.Name)")
        }
    }
    foreach ($requiredName in $script:AllowedTopLevelNames) {
        if (-not (Test-Path -LiteralPath (Join-Path $Root $requiredName))) {
            $violations.Add("missing required entry: $requiredName")
        }
    }
    foreach ($item in Get-ChildItem -LiteralPath $Root -Force -Recurse) {
        if (Test-SamePath $item.FullName $Root) {
            continue
        }
        $relative = Get-RelativePath -Root $Root -FullName $item.FullName
        $violation = Test-ReleaseRelativePath -RelativePath $relative -IsDirectory $item.PSIsContainer
        if ($null -ne $violation) {
            $violations.Add("$relative ($violation)")
        }
    }
    if ($violations.Count -gt 0) {
        throw "The release output contains forbidden entries:`n$($violations -join "`n")"
    }
}

function Assert-CompiledClasses {
    param(
        [Parameter(Mandatory = $true)][string]$OverridesRoot,
        [Parameter(Mandatory = $true)][string[]]$ClassList,
        [Parameter(Mandatory = $true)][string]$JavapPath
    )
    foreach ($entry in $ClassList) {
        $relativeSource = $entry.Replace('/', '\').Substring(4)
        $firstSeparator = $relativeSource.IndexOf('\')
        if ($firstSeparator -lt 0) {
            throw "Could not derive the class name from class list entry: $entry"
        }
        $classRelative = $relativeSource.Substring($firstSeparator + 1)
        $classRelative = [System.IO.Path]::ChangeExtension($classRelative, '.class')
        $classPath = Join-Path $OverridesRoot $classRelative
        if (-not (Test-Path -LiteralPath $classPath -PathType Leaf)) {
            throw "Compilation did not produce the expected class for $entry"
        }
    }
    $nonClassFiles = @(Get-ChildItem -LiteralPath $OverridesRoot -File -Recurse -Force | Where-Object { $_.Extension -ine '.class' })
    if ($nonClassFiles.Count -gt 0) {
        throw "client-overrides contains non-class files:`n$($nonClassFiles.FullName -join "`n")"
    }
    $badClasses = @(Get-ChildItem -LiteralPath $OverridesRoot -File -Recurse -Force | Where-Object { $_.Name.Split('$')[0] -match '(?i)Smoke|Probe' })
    if ($badClasses.Count -gt 0) {
        throw "client-overrides contains smoke or probe classes:`n$($badClasses.FullName -join "`n")"
    }

    $devModeClass = Join-Path $OverridesRoot 'zlport\dev\DevMode.class'
    $devOptionsClass = Join-Path $OverridesRoot 'zlport\dev\LauncherDevOptions.class'
    if (-not (Test-Path -LiteralPath $devModeClass -PathType Leaf)) {
        throw 'The release DevMode class was not compiled.'
    }
    if (-not (Test-Path -LiteralPath $devOptionsClass -PathType Leaf)) {
        throw 'The release LauncherDevOptions class was not compiled.'
    }

    $devModeResult = Invoke-Native -FilePath $JavapPath -Arguments @('-classpath', $OverridesRoot, '-c', '-p', 'zlport.dev.DevMode')
    if ($devModeResult.ExitCode -ne 0) {
        throw "javap failed for zlport.dev.DevMode: $($devModeResult.Output -join "`n")"
    }
    $devModeText = $devModeResult.Output -join "`n"
    if ($devModeText -match 'iconst_1') {
        throw 'DevMode.enabled() is not the no-op release implementation.'
    }
    if ($devModeText -notmatch 'enabled\(\);[\s\S]*?iconst_0') {
        throw 'DevMode.enabled() does not return false in the release build.'
    }
    if ($devModeText -notmatch 'banner\(\);[\s\S]*?// String\s*(?:\r?\n|$)') {
        throw 'DevMode.banner() does not return an empty string in the release build.'
    }

    $devOptionsResult = Invoke-Native -FilePath $JavapPath -Arguments @('-classpath', $OverridesRoot, '-c', '-p', 'zlport.dev.LauncherDevOptions')
    if ($devOptionsResult.ExitCode -ne 0) {
        throw "javap failed for zlport.dev.LauncherDevOptions: $($devOptionsResult.Output -join "`n")"
    }
    $devOptionsText = $devOptionsResult.Output -join "`n"
    if ($devOptionsText -match 'JTextField|Directorio de misiones') {
        throw 'LauncherDevOptions is not the no-op release implementation.'
    }

    $classCount = @(Get-ChildItem -LiteralPath $OverridesRoot -File -Recurse -Force | Where-Object { $_.Extension -ieq '.class' }).Count
    return $classCount
}

function Assert-SmokeTest {
    param(
        [Parameter(Mandatory = $true)][string]$OutputRoot,
        [Parameter(Mandatory = $true)][string]$JavaPath
    )
    $arguments = @(
        '-Djava.awt.headless=true',
        "-Dzlport.ios.bundle=$OutputRoot\game-assets-ios",
        '-cp',
        "$OutputRoot\client-overrides;$OutputRoot\lib\*",
        'zlport.launcher.IosMain',
        '--help'
    )
    $result = Invoke-Native -FilePath $JavaPath -Arguments $arguments
    foreach ($line in $result.Output) {
        Write-Output ("SMOKE: " + $line)
    }
    if ($result.ExitCode -ne 0) {
        throw "The headless launcher smoke test failed with exit code $($result.ExitCode)."
    }
    $text = $result.Output -join "`n"
    if ($text -match '\[dev\]' -or $text -match 'ganchos de desarrollo') {
        throw 'The headless launcher smoke test detected development mode in the release.'
    }
    if ($text -notmatch 'Uso: zombie-lane-ios') {
        throw 'The headless launcher smoke test did not print the expected help text.'
    }
}

$repositoryRoot = Get-NormalizedPath (Join-Path $PSScriptRoot '..')
if ($Version -notmatch '^[0-9A-Za-z][0-9A-Za-z._-]*$' -or $Version.Length -gt 64) {
    throw 'Version must start with a letter or digit and may contain only letters, digits, dots, underscores, and hyphens.'
}
if ([string]::IsNullOrWhiteSpace($OutputRoot)) {
    $outputRoot = Get-NormalizedPath (Join-Path (Split-Path -Parent $repositoryRoot) ('Zombie-Lane-Alpha-' + $Version))
} else {
    $outputRoot = Get-NormalizedPath $OutputRoot
}
$assetsRoot = Get-NormalizedPath $AssetsRoot

$repositoryInfo = Assert-RepositoryPreconditions -RepositoryRoot $repositoryRoot
$assetInfo = Assert-Assets -AssetsRoot $assetsRoot
$compilerInfo = Resolve-Compiler -AssetsRoot $assetsRoot -JavacPath $JavacPath
$runtimeVersion = Get-JavaVersion -Java $assetInfo.JavaPath
if ($null -eq $runtimeVersion) {
    throw "Could not determine the version of the bundled Java runtime: $($assetInfo.JavaPath)"
}
if ($runtimeVersion.Major -lt 21) {
    throw "The bundled Java runtime must be version 21 or newer; it reports $($runtimeVersion.Version)."
}
Assert-OutputRootSafe -OutputRoot $outputRoot -RepositoryRoot $repositoryRoot -AssetsRoot $assetsRoot

Write-Output ("Using javac: {0} ({1})" -f $compilerInfo.Path, $compilerInfo.Version)
Write-Output ("Using java: {0} ({1})" -f $assetInfo.JavaPath, $runtimeVersion.Version)
Write-Output ("Using assets: {0} ({1} JARs)" -f $assetsRoot, $assetInfo.JarCount)

$overridesRoot = Join-Path $repositoryRoot 'client-overrides'
if (Test-Path -LiteralPath $overridesRoot) {
    Remove-Item -LiteralPath $overridesRoot -Recurse -Force
}
New-Item -ItemType Directory -Path $overridesRoot | Out-Null

$sourceListPath = Join-Path $env:TEMP ('zombie-lane-sources-' + [guid]::NewGuid().ToString('N') + '.txt')
try {
    $sourceListLines = foreach ($entry in $repositoryInfo.ClassList) {
        $fullSourcePath = Join-Path $repositoryRoot $entry.Replace('/', '\')
        '"' + $fullSourcePath.Replace('\', '/') + '"'
    }
    [System.IO.File]::WriteAllText(
        $sourceListPath,
        ($sourceListLines -join "`r`n") + "`r`n",
        (New-Object System.Text.UTF8Encoding($false))
    )
    $compilerArguments = @(
        '--release', '21',
        '-encoding', 'UTF-8',
        '-proc:none',
        '-cp', (Join-Path $assetsRoot 'lib\*'),
        '-sourcepath', ("$repositoryRoot\src;$repositoryRoot\src-release"),
        '-d', $overridesRoot,
        ('@' + $sourceListPath)
    )
    $compilerResult = Invoke-Native -FilePath $compilerInfo.Path -Arguments $compilerArguments
    foreach ($line in $compilerResult.Output) {
        Write-Output $line
    }
    if ($compilerResult.ExitCode -ne 0) {
        throw "Compilation failed with exit code $($compilerResult.ExitCode)."
    }
} finally {
    if (Test-Path -LiteralPath $sourceListPath) {
        Remove-Item -LiteralPath $sourceListPath -Force
    }
}

$compiledClassCount = Assert-CompiledClasses -OverridesRoot $overridesRoot -ClassList $repositoryInfo.ClassList -JavapPath $compilerInfo.JavapPath
Write-Output ("Compiled classes: {0}" -f $compiledClassCount)

if (Test-Path -LiteralPath $outputRoot) {
    Remove-Item -LiteralPath $outputRoot -Recurse -Force
}
New-Item -ItemType Directory -Path $outputRoot | Out-Null
Copy-AssetTree -Source (Join-Path $assetsRoot 'runtime') -Destination (Join-Path $outputRoot 'runtime')
Copy-AssetTree -Source (Join-Path $assetsRoot 'lib') -Destination (Join-Path $outputRoot 'lib')
Copy-AssetTree -Source (Join-Path $assetsRoot 'game-assets-ios') -Destination (Join-Path $outputRoot 'game-assets-ios')
Copy-Item -LiteralPath $overridesRoot -Destination (Join-Path $outputRoot 'client-overrides') -Recurse -Force

$batchContents = @(
    '@echo off',
    'setlocal',
    'cd /d "%~dp0"',
    '"runtime\bin\java.exe" "-Dzlport.server.url=http://127.0.0.1:6767/dead/" "-Dzlport.ios.bundle=%~dp0game-assets-ios" -cp "client-overrides;lib\*" zlport.launcher.IosMain',
    'set "ZL_EXIT=%ERRORLEVEL%"',
    'if not "%ZL_EXIT%"=="0" pause',
    'exit /b %ZL_EXIT%'
) -join "`r`n"
[System.IO.File]::WriteAllText(
    (Join-Path $outputRoot 'Run Zombie Lane Alpha.bat'),
    $batchContents + "`r`n",
    (New-Object System.Text.UTF8Encoding($false))
)

$sourceStateText = if ($repositoryInfo.SourceState -eq 'clean') { 'clean' } else { 'dirty (development build)' }
$builtUtc = [DateTime]::UtcNow.ToString('yyyy-MM-ddTHH:mm:ssZ')
$versionContents = @(
    "Zombie Lane Revival - Alpha $Version",
    'Edition: player (release, dev hooks disabled)',
    "Built (UTC): $builtUtc",
    "Source branch: $($repositoryInfo.Branch)",
    "Source commit: $($repositoryInfo.HeadShort) ($($repositoryInfo.HeadFull))",
    "Source state: $sourceStateText",
    "Compiler: javac $($compilerInfo.Version) (--release 21, UTF-8)",
    "Runtime: java $($runtimeVersion.Version)",
    "Class list: tools/client-classes.txt ($($repositoryInfo.ClassList.Count) entries, $compiledClassCount classes)",
    'Server: not included',
    'Default server URL: http://127.0.0.1:6767/dead/',
    'Contents: runtime, lib, game-assets-ios, client-overrides, launcher'
) -join "`r`n"
[System.IO.File]::WriteAllText(
    (Join-Path $outputRoot 'VERSION.txt'),
    $versionContents + "`r`n",
    (New-Object System.Text.UTF8Encoding($false))
)

$releaseNotes = @"
Zombie Lane Revival - Alpha $Version
========================================

This package contains the player edition of the client. It does not include the
server or any server data.

Changes in this build:
- Dev mode is compiled out: the release uses the no-op DevMode and
  LauncherDevOptions classes, so the "[dev]" banner and dev-only launcher
  controls are not present.
- The launcher asks for a language on first run (English, French, Italian,
  German or Spanish), stores it locally in launcher.properties and applies it
  to the selected profile. No language data is sent to the server.
- The launcher fills option fields from defaults when no local profile is
  available, and reports width and height independently. Duplicate
  zombie-lane.properties warnings are no longer shown.
- The Play button stays enabled and validates the form before launch.
- The build compiles the explicit public client class list with the release
  stubs instead of scanning every source file in the tree.
- Release assembly rejects server code and data, sources, tools, tests, docs,
  build scripts, dev classes, smoke/probe classes, and archives.
- Version: $Version.

Running:
- Double-click "Run Zombie Lane Alpha.bat" or run it from a terminal.
- The launcher defaults to http://127.0.0.1:6767/dead/. Change the server in
  the launcher or pass -Dzlport.server.url=... to java.
- A Java runtime is bundled in runtime/; no system Java installation is
  required.

See the repository commits for the gameplay changes included in this version.
"@
[System.IO.File]::WriteAllText(
    (Join-Path $outputRoot 'RELEASE-NOTES.txt'),
    $releaseNotes.Replace("`n", "`r`n").TrimEnd() + "`r`n",
    (New-Object System.Text.UTF8Encoding($false))
)

Assert-NoForbiddenEntries -Root $outputRoot

if (-not $SkipSmoke) {
    Assert-SmokeTest -OutputRoot $outputRoot -JavaPath $assetInfo.JavaPath
} else {
    Write-Output 'SMOKE: skipped (-SkipSmoke)'
}

$zipPath = $null
$zipLength = $null
$zipSha256 = $null
if (-not $SkipZip) {
    $archiveFolderName = 'Zombie-Lane-Alpha-' + $Version
    $zipPath = Join-Path (Split-Path -Parent $outputRoot) ($archiveFolderName + '.zip')
    if (Test-PathWithin -Path $zipPath -Parent $outputRoot) {
        throw "The release zip must not be inside OutputRoot: $zipPath"
    }
    Add-Type -AssemblyName System.IO.Compression
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $zipStream = [System.IO.File]::Open($zipPath, [System.IO.FileMode]::Create, [System.IO.FileAccess]::ReadWrite, [System.IO.FileShare]::None)
    try {
        $archive = New-Object System.IO.Compression.ZipArchive($zipStream, [System.IO.Compression.ZipArchiveMode]::Create, $true)
        try {
            $null = $archive.CreateEntry($archiveFolderName + '/')
            foreach ($directory in Get-ChildItem -LiteralPath $outputRoot -Directory -Force -Recurse | Sort-Object FullName) {
                $relativeDirectory = Get-RelativePath -Root $outputRoot -FullName $directory.FullName
                $null = $archive.CreateEntry(($archiveFolderName + '/' + $relativeDirectory.Replace('\', '/') + '/'))
            }
            foreach ($file in Get-ChildItem -LiteralPath $outputRoot -File -Force -Recurse | Sort-Object FullName) {
                $relativeFile = Get-RelativePath -Root $outputRoot -FullName $file.FullName
                $entry = $archive.CreateEntry(($archiveFolderName + '/' + $relativeFile.Replace('\', '/')), [System.IO.Compression.CompressionLevel]::Optimal)
                $entryStream = $entry.Open()
                try {
                    $inputStream = [System.IO.File]::OpenRead($file.FullName)
                    try {
                        $inputStream.CopyTo($entryStream)
                    } finally {
                        $inputStream.Dispose()
                    }
                } finally {
                    $entryStream.Dispose()
                }
            }
        } finally {
            $archive.Dispose()
        }
    } finally {
        $zipStream.Dispose()
    }

    $readArchive = [System.IO.Compression.ZipFile]::OpenRead($zipPath)
    try {
        $zipViolations = New-Object 'System.Collections.Generic.List[string]'
        foreach ($entry in $readArchive.Entries) {
            $entryName = $entry.FullName
            if ($entryName -match '\\' -or $entryName.StartsWith('/') -or $entryName -match '^[A-Za-z]:' -or $entryName -match '(^|/)\.\.?(/|$)') {
                $zipViolations.Add("$entryName (invalid path)")
                continue
            }
            $parts = @($entryName -split '/' | Where-Object { $_ -ne '' })
            if ($parts.Count -eq 0) {
                continue
            }
            if ($parts[0] -ne $archiveFolderName) {
                $zipViolations.Add("$entryName (unexpected top-level folder)")
                continue
            }
            if ($parts.Count -eq 1) {
                if (-not $entryName.EndsWith('/')) {
                    $zipViolations.Add("$entryName (top-level file)")
                }
                continue
            }
            $topLevelName = $parts[1]
            $allowedTopLevel = $false
            foreach ($allowedName in $script:AllowedTopLevelNames) {
                if ($topLevelName -ieq $allowedName) {
                    $allowedTopLevel = $true
                    break
                }
            }
            if (-not $allowedTopLevel) {
                $zipViolations.Add("$entryName (unexpected top-level entry)")
                continue
            }
            $relative = ($parts[1..($parts.Count - 1)] -join '\')
            $isDirectory = $entryName.EndsWith('/')
            $violation = Test-ReleaseRelativePath -RelativePath $relative -IsDirectory $isDirectory
            if ($null -ne $violation) {
                $zipViolations.Add("$entryName ($violation)")
            }
        }
        if ($zipViolations.Count -gt 0) {
            throw "The release zip contains forbidden entries:`n$($zipViolations -join "`n")"
        }
        $topLevelNames = @($readArchive.Entries | ForEach-Object { ($_.FullName -split '/')[0] } | Sort-Object -Unique)
        if ($topLevelNames.Count -ne 1 -or $topLevelNames[0] -ne $archiveFolderName) {
            throw 'The release zip does not have exactly the required top-level folder.'
        }
        $expectedFiles = New-Object 'System.Collections.Generic.HashSet[string]' ([System.StringComparer]::OrdinalIgnoreCase)
        foreach ($file in Get-ChildItem -LiteralPath $outputRoot -File -Force -Recurse) {
            $null = $expectedFiles.Add((Get-RelativePath -Root $outputRoot -FullName $file.FullName))
        }
        $zipFiles = @($readArchive.Entries | Where-Object { -not $_.FullName.EndsWith('/') } | ForEach-Object { $_.FullName.Substring($archiveFolderName.Length + 1).Replace('/', '\') })
        if ($zipFiles.Count -ne $expectedFiles.Count) {
            throw "The release zip file count ($($zipFiles.Count)) does not match the output file count ($($expectedFiles.Count))."
        }
        foreach ($zipFile in $zipFiles) {
            if (-not $expectedFiles.Contains($zipFile)) {
                throw "The release zip contains an unexpected file: $zipFile"
            }
        }
        Write-Output ("ZIP files verified: {0}" -f $expectedFiles.Count)
    } finally {
        $readArchive.Dispose()
    }
    $zipInfo = Get-Item -LiteralPath $zipPath
    $zipLength = $zipInfo.Length
    $zipSha256 = (Get-FileHash -LiteralPath $zipPath -Algorithm SHA256).Hash
}

Write-Output 'BUILD SUMMARY:'
Write-Output ("Version: {0}" -f $Version)
Write-Output ("Source branch: {0}" -f $repositoryInfo.Branch)
Write-Output ("Source commit: {0}" -f $repositoryInfo.HeadShort)
Write-Output ("Source state: {0}" -f $repositoryInfo.SourceState)
Write-Output ("Compiler: javac {0}" -f $compilerInfo.Version)
Write-Output ("Runtime: java {0}" -f $runtimeVersion.Version)
Write-Output ("Classes: {0}" -f $compiledClassCount)
foreach ($folder in Get-ChildItem -LiteralPath $outputRoot -Directory -Force | Sort-Object Name) {
    $fileCount = @(Get-ChildItem -LiteralPath $folder.FullName -File -Recurse -Force).Count
    Write-Output ("{0}: {1} files" -f $folder.Name, $fileCount)
}
$topLevelFiles = @(Get-ChildItem -LiteralPath $outputRoot -File -Force)
if ($topLevelFiles.Count -gt 0) {
    Write-Output ("(root files): {0} files" -f $topLevelFiles.Count)
}
$totalBytes = (Get-ChildItem -LiteralPath $outputRoot -File -Force -Recurse | Measure-Object -Property Length -Sum).Sum
if ($null -eq $totalBytes) {
    $totalBytes = 0
}
Write-Output ("Output: {0}" -f $outputRoot)
Write-Output ("Total release size: {0} bytes" -f $totalBytes)
if ($SkipZip) {
    Write-Output 'Zip: skipped (-SkipZip)'
} else {
    Write-Output ("Zip path: {0}" -f $zipPath)
    Write-Output ("Zip size: {0} bytes" -f $zipLength)
    Write-Output ("Zip SHA-256: {0}" -f $zipSha256)
}
