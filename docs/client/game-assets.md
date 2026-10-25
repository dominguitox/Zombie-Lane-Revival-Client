# `game-assets-ios` Graphics Assets

**Applies to:** the shipped `game-assets-ios` bundle and the `0.1.2` loader sources. Last reviewed: 2026-10-25.


This document describes how the Java iOS port locates, interprets, loads,
draws, and releases sprites from the `game-assets-ios` directory.

## Resource location and resolution

The launcher installs the platform with a bundle directory. By default,
`IosMain` looks for `game-assets-ios` next to the installation; this can be
overridden with `-Dzlport.ios.bundle=DIR`. `PcIosPlatform` normalizes this path
and uses it as the root for game resources.

Game resources are not all standalone PNGs. `Toolkit` opens files by name
(for example, `p`, `bundle_0/0`, or `bundle_12/0`) as well as numeric
resources. Numeric resources can be standalone files or entries in packed
`rN` files; the latter contain an offset table used to extract each entry.
Animations and bundle tables are read with `Toolkit.getResourceByteArray(...)`.

To open a named resource, `PcIosPlatform.openResource` searches the bundle
first and then `Library/Caches`. For PNG textures, the platform appends
`.png` to the requested name and uses the same search order. The installed
copy therefore takes precedence over the cached version.

## Toolkit resource containers

`Toolkit` has two ways to resolve a numeric resource ID:

- If the unsigned high 16 bits are zero, the low 16 bits name a standalone
  file such as `1234`.
- Otherwise, the high 16 bits select a packed file named `r<high-bits>` and
  the low 16 bits select an entry in that file.
- Resource ID `-1` is treated as a missing-resource sentinel by the byte
  retrieval methods.

`Toolkit.loadFile()` reads packed files using a four-byte big-endian length
prefix. That length includes the prefix: the implementation allocates that
many bytes, keeps the prefix at offsets 0-3, and reads the rest of the file
into the same buffer. `DChocByteArray` confirms that multi-byte integers and
shorts in these records are big-endian.

The packed-file index is read directly from the complete buffer:

| Byte offset | Value |
| --- | --- |
| 0-3 | Declared total file length, including this prefix |
| 4-7 | Number of entries |
| 8 onward | One 32-bit absolute file offset per entry |

Entry `i` starts at the offset stored at `8 + 4*i` and ends at the next entry's
offset. The last entry ends at the end of the packed file. Thus the low 16-bit
part of an ID indexes the offset table, not a byte location. The source does
not validate these offsets before copying the entry.

`Toolkit` keeps one packed file in `sm_loadedFileName` and
`sm_loadedFileData`. Loading another `rN` replaces that single-file buffer;
this is a read cache, not a cache of all decoded resources. Named bundle files
such as `bundle_<id>/0` bypass this numeric `rN` lookup and are opened by
their path.

## Bundle and atlas initialization

`DavinciUtilities.initialize()` prepares the texture tables with
`textureInit()` and starts `ServerAssetLoader`.

1. `textureInit()` reads `bundle_0/0` to determine the number of bundles and
   textures, and obtains the local, remote, and excluded bundle lists from
   numeric resources `80407`, `80409`, and `80408`.
2. For each non-excluded local bundle, it reads its index file `0`. The path
   is `bundle_<id>/0` for local bundles or
   `bundle_<id>/<version>/0` for server-versioned bundles.
3. `readTextureBundleData()` records the color depth, texture count, and atlas
   region coordinates. This data maps each game image index to a texture and
   a rectangular region.
4. The bundle state is recorded as local or pending from the server.
   `Animation` can use an alternate variant while waiting for the required
   bundle and register a listener to react when it loads.

Server textures are downloaded as
`bundle_<id>/<version>/<n>.png` and stored in the cache. `ServerAssetLoader`
builds the queue from resource `80409`; network loading is enabled only when
the device uses HD graphics. When a bundle finishes loading, its state is
updated and its listeners are notified.

### Bundle index record

The `readTextureBundleData()` reader consumes the following fields from a
bundle index buffer. Multi-byte numbers are big-endian.

| Field | Reader type | Meaning established by the code |
| --- | --- | --- |
| Bundle ID | 32-bit signed integer | Index into the bundle state and texture-index arrays |
| Skipped value | 32-bit signed integer | Read and discarded; meaning not established |
| Skipped value | 8-bit signed byte | Read and discarded; meaning not established |
| Color depth | 8-bit signed byte | Stored for subsequent PNG texture loading |
| Texture count | 16-bit signed short | Number of texture records that follow |
| Region count per texture | 16-bit signed short | Number of atlas regions in that texture's coordinate table |
| Region rectangle | Four 16-bit signed shorts | Stored, in order, as the region's four coordinates/dimensions |

For the bundle-0 index, `textureInit()` first consumes a 32-bit field that it
increments by one for the bundle-array length, plus a 16-bit texture count;
it then passes the remaining bytes to this reader. Other local bundle indexes
are passed directly to it. The code does not document the skipped index
fields' semantics or provide an independent format version/schema.

The numeric resource `80409` is read in two compatible-looking contexts:
`textureInit()` consumes a bundle ID, version, and a third integer per entry;
`ServerAssetLoader.init()` interprets that third integer as the number of
texture PNGs in the bundle. For each such texture, the loader expects
`1.png` through `<count>.png`, followed by the index file `0`. The first
reader discards the count after identifying the server bundle.

## From a resource ID to a texture region

Game calls such as `new SpriteObject(resourceId, ...)` or
`AnimationsManager.loadShared(resourceId)` reach
`DavinciUtilities.loadAnimation()` and construct an `Animation`.

An animation's format may begin with an SD/HD variant descriptor. `Animation`
chooses the resource ID corresponding to the graphics profile and device; the
selected content describes the frames, their durations, and their renderable
elements. When an image element is processed, `AnimationFrame` passes its ID
to `DavinciUtilities.loadImageIntoCache()`.

The image ID encodes the data used to resolve the atlas:

- `id >> 20` identifies the bundle.
- `(id & 0xFFFFF) >> 14` identifies the texture number within the bundle
  (indexed from 1).
- `(id & 0x3FFF) * 4` selects the four region coordinates in the atlas table.

Using these parts, `loadTextureImage()` locates the texture and creates an
`OGLRegion` with the sprite's position and size within it. The image is loaded
lazily the first time it is needed, rather than while all animations are
being read.

## From a world object to its bundle

A world object does **not** have one bundle ID stored on the object or in its
save record. The port resolves its graphics through a chain of definition and
resource IDs:

1. A saved scene object contains an item type and item ID, followed by its
   object type, instance ID, tile coordinates, and type-specific state.
   `ObjectLoader` resolves the type/ID pair through `ItemManager` and creates
   the appropriate object (`ConstructionObject`, `ZombieCharacter`,
   `LootObject`, `DecorationObject`, and so on).
2. The resolved item definition contains one or more animation resource IDs.
   For example, decoration and construction definitions read an icon
   animation ID, an animation-ID array, collision animation IDs, and (for
   constructions) shadow animation IDs. `ToolkitHelpers.readResourceIDs()`
   reads an array as a 32-bit count followed by that many 32-bit IDs.
3. The item's animation wrapper chooses an animation from that array based on
   the object's state, direction, or other game logic. `AnimationsManager`
   loads the chosen animation resource ID. That resource ID locates a
   standalone numeric resource or a packed `rN` entry; it is **not** itself a
   bundle number.
4. The animation resource header contains a bundle ID used to determine
   whether its required bundle is local or still pending. The animation
   frames can then reference image resource IDs. Each image resource ID
   independently encodes the texture bundle, texture number, and atlas
   region, as described above.
5. A frame can also reference nested animations or particle animations.
   Those add more animation and image resources to the rendered object.

Consequently, a single visible object can use multiple animation resources
and multiple texture bundles: for example, its current animation, a shadow,
an equipped/paper-doll layer, and nested effects need not share an atlas.
Different animation states can also select different entries from the item's
animation array. To determine every bundle used by an object, follow all
resource IDs reachable from its definition and selected animation, including
nested/particle animations, and decode the bundle field from every referenced
image ID. The item ID or instance ID alone cannot answer that question.

For an image resource ID, the reader's inverse field layout is:

```text
bundleId     = imageId >> 20
textureNo    = (imageId & 0xFFFFF) >> 14   // 1-based within the bundle
regionIndex  = imageId & 0x3FFF            // 0-based within the texture
```

For example, the *illustrative* image ID `0x708003` resolves to bundle `7`,
texture `2`, region `3`. This is an example of the bit layout, not a claim
that the installed game contains that exact reference. The bundle index maps
`textureNo - 1` to the texture table; `regionIndex * 4` selects the four
stored region values.

Do not conflate these three IDs:

| ID | What it identifies | Where it is read |
| --- | --- | --- |
| World item ID | Which catalog definition/object kind is instantiated | Scene record, then `ItemManager` |
| Animation resource ID | Which animation definition/resource bytes to parse | Item definition, nested frame record, or game code |
| Image resource ID | Which bundle texture and atlas region to draw | Type-1 image renderable in an animation frame |

The animation header's bundle ID is useful for pending-bundle notification
and SD/HD fallback. The low-level texture lookup, however, takes the bundle
ID from each image resource ID. The header alone is therefore not a complete
inventory of every texture bundle used by a complex animation.

## Animation and frame binary records

`Animation` reads a marker byte at the start of the resource. When it is `1`,
the marker is followed by a 32-bit variant count and that many pairs of
32-bit values (variant type, resource ID). Type `1` is treated as SD and type
`2` as HD. If the original resource is selected, the bundle ID follows the
variant table. If another resource is selected, the reader opens it, consumes
its first byte, and then reads the bundle ID. When the original marker is not
`1`, it is consumed as the direct-form marker and the bundle ID follows.
Other marker values are not described separately by the source.

The animation body starts with:

| Field | Encoding / interpretation |
| --- | --- |
| Flags | Unsigned byte. Bit 0 selects coordinate width: clear means byte coordinates, set means short coordinates. Bit 1 similarly selects byte or short frame durations. |
| Frame count | Unsigned byte |
| Loop preference | Boolean byte |
| Continuous nested animations | Boolean byte |
| Bounds | `x`, `y`, `width`, `height`; signed coordinates and unsigned dimensions for byte form, signed shorts for short form |
| Alignment | Unsigned byte |
| Frame references | One unsigned byte per frame; non-reference frames contain frame data, then references are linked to their target frames |
| Frame durations | One unsigned byte or unsigned short per frame, selected by flag bit 1; a stored zero is treated as an effectively unbounded duration |
| Timeline-present | Boolean byte |

If a timeline is present, the stream contains timeline bounds in the coordinate
width selected by flag bit 0, followed by six timeline channels. Each channel
starts with an unsigned-byte event count; each event contains a 32-bit time,
an unsigned-byte interpolation mode, and a value. Color, scale, and rotation
values are 32-bit integers; position X/Y values use signed bytes or signed
shorts according to the coordinate-width flag. The source names the channels
as color, X, Y, width scale, height scale, and rotation.

After the optional timeline, each non-reference frame is read in order. A
frame contains:

1. Width and height in the selected coordinate width.
2. An unsigned-byte collision-box count. Each box contains an unsigned-byte
   ID and `x`, `y`, `width`, and `height`; in byte-coordinate form, positions
   are signed and dimensions unsigned, while all four values are signed
   shorts in short-coordinate form.
3. An unsigned-short renderable count, followed by that many renderable
   records.

The observed renderable record layouts are:

| Type | Data consumed by the reader |
| --- | --- |
| 1 — image | `x`, `y`; packed unsigned byte; 32-bit image ID; unsigned-byte palette/image index. The packed byte's low nibble is the transform and its high nibble plus one is the scale. |
| 2 — filled rectangle | 32-bit color followed by `x`, `y`, `width`, and `height`; byte-form dimensions are unsigned. |
| 3 — rectangle outline | Same field layout as type 2. |
| 4 — line | 32-bit color followed by two signed endpoint coordinate pairs. |
| 5 — filled ellipse | 32-bit color followed by `x`, `y`, `width`, and `height`; byte-form dimensions are unsigned. |
| 6 — filled polygon | 32-bit color; signed-short point count; that many coordinate pairs in byte or short form. |
| 8 — filled Bezier | Same point-count and coordinate layout as type 6, rendered as a Bezier. |
| 7 — clip rectangle | `x`, `y`, `width`, `height` in byte or short form. |
| 9 — nested animation | `x`, `y`; one unsigned byte that is discarded; 32-bit nested animation resource ID. |
| 10 — particle emitter | Signed-short animation count and 32-bit animation IDs, emitter bounds, five boolean options, then emitter ID/count, gravity, acceleration, angle, spread, velocity, lifetime, duration, and emission-interval values. |

For types 2-5, the reader stores the color separately from the geometry. For
types 6 and 8 it stores the points separately. The parser's default branch
also reads the primitive-shaped layout for any other type; the source does
not establish that unknown types are valid or compatible.

This is a reader-derived layout, not a complete authoring specification. In
particular, meanings of skipped bytes/integers, exact semantic names for every
primitive type, and constraints on valid offsets, counts, coordinates, and
references are not fully defined by these Java readers.

## Decoding and uploading to OpenGL

`OGLTextureManager` delegates loading to `IosPlatform.loadTexture()`. In this
port, `PcIosPlatform` reads the PNG bytes and `IosTextureLoader`:

1. decodes the PNG with `ImageIO`;
2. converts its pixels to RGBA with premultiplied alpha;
3. converts the pixels to the color depth declared by the bundle:
   8888, 5551, 4444, or 565;
4. creates a 2D OpenGL texture with linear filtering and `CLAMP_TO_EDGE`
   wrapping, and returns its ID and dimensions.

`OGLTexture` stores this data; `OGLRegion` adds the sprite-specific rectangle.
This allows multiple sprites to share the same PNG and use different atlas
regions.

## Animation playback and drawing

`SpriteObject.logicUpdate(delta)` advances animation time, changes frames
according to their durations, and applies the loop mode. When drawing a frame,
`AnimationFrame.doDraw()` iterates over its renderable elements in order. For
an image element, it retrieves the cached region and calls
`OGL.drawTexture(...)`; primitives, clipping, nested animations, and particles
are processed in the same iteration.

The OpenGL path binds the corresponding texture and draws a quad using the
region's texture coordinates. Element transformations (such as horizontal and
vertical flips), position, and clipping are applied during drawing.

## References and release

`DavinciUtilities` maintains region and image caches and reference counters
for frame elements. `AnimationsManager.loadShared()` shares `SpriteObject`
instances by resource ID and tracks their users; when they are unloaded,
`unloadShared()` frees the sprite when its count reaches zero.

When no frames reference an image, `releaseUnreferencedImages()` removes it
from the caches and unloads its OpenGL texture through
`OGLTextureManager.unloadTexture()`. This allows an entire atlas texture to be
released when none of its regions are still in use.

## Main source files

- [Toolkit.java](../../src/game-ios/com/dchoc/toolkit/Toolkit.java): reads
  standalone, packed, and bundle resources. The byte reader is
  [DChocByteArray.java](../../src/compat-ios/com/dchoc/toolkit/DChocByteArray.java).
- [DavinciUtilities.java](../../src/game-ios/com/dchoc/spriteobject/DavinciUtilities.java):
  initializes bundles, IDs, the atlas, and image caches.
- [ObjectLoader.java](../../src/game-ios/com/dchoc/idead/isometric/ObjectLoader.java),
  [ItemManager.java](../../src/game-ios/com/dchoc/idead/items/ItemManager.java),
  [Item.java](../../src/game-ios/com/dchoc/idead/items/Item.java), and
  [ToolkitHelpers.java](../../src/game-ios/com/dchoc/toolkit/ToolkitHelpers.java):
  map persistent world-object item IDs to item definitions and animation ID
  arrays.
- [Animation.java](../../src/game-ios/com/dchoc/spriteobject/Animation.java) and
  [AnimationFrame.java](../../src/game-ios/com/dchoc/spriteobject/AnimationFrame.java):
  select variants and read and draw frames.
- [TimelineChannel.java](../../src/game-ios/com/dchoc/spriteobject/TimelineChannel.java):
  reads and evaluates animation timeline events.
- [SpriteObject.java](../../src/game-ios/com/dchoc/spriteobject/SpriteObject.java)
  and
  [AnimationsManager.java](../../src/game-ios/com/dchoc/idead/animation/AnimationsManager.java):
  advance animation time and manage shared references.
- [ServerAssetLoader.java](../../src/game-ios/com/dchoc/spriteobject/ServerAssetLoader.java)
  and
  [ServerBundle.java](../../src/game-ios/com/dchoc/spriteobject/ServerBundle.java):
  load and cache server bundles.
- [PcIosPlatform.java](../../src/runtime-ios/zlport/ios/runtime/PcIosPlatform.java)
  and
  [IosTextureLoader.java](../../src/runtime-ios/zlport/ios/runtime/IosTextureLoader.java):
  resolve files, decode PNGs, and upload textures.
