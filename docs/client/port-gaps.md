# Stubbed methods in the port

**Applies to:** the `0.1.2` source tree. Last reviewed: 2026-10-25.


The counts and method names below come from the decompiled source tree under [`src/`](../../src/). They describe the port, not the original game. [hypothesis: port]

| Source file | Stub count | Probable role (guess from name only) |
|---|---:|---|
| [`collection/List.java`](../../src/game-ios/com/dchoc/collection/List.java) | 17 [hypothesis: port] | UI or collection list behavior. [hypothesis: port] |
| [`idead/ScalingButton.java`](../../src/game-ios/com/dchoc/idead/ScalingButton.java) | 13 [hypothesis: port] | Scaled interactive button behavior. [hypothesis: port] |
| [`toolkit/ServerSocketMessage.java`](../../src/game-ios/com/dchoc/toolkit/ServerSocketMessage.java) | 15 [hypothesis: port] | Network message serialization and parsing. [hypothesis: port] |
| [`toolkit/Toolkit.java`](../../src/game-ios/com/dchoc/toolkit/Toolkit.java) | 3 [hypothesis: port] | Shared toolkit functions, including image and rendering operations. [hypothesis: port] |
| [`spriteobject/ServerBundle.java`](../../src/game-ios/com/dchoc/spriteobject/ServerBundle.java) | 2 [hypothesis: port] | Server-delivered asset bundle callbacks. [hypothesis: port] |
| **Total** | **50** [hypothesis: port] | |

Each listed method throws `UnsupportedOperationException` with the message prefix `sem código na 1.4.4`. [hypothesis: port]

Reproduce the count in PowerShell with `rg -o 'sem código na 1\.4\.4' src | Measure-Object -Line`; the matching source lines report 50 occurrences. [hypothesis: port]

## Method names

- `List`: `append`, `delete`, `deleteAll`, `getFitPolicy`, `getFont`, `getImage`, `getSelectedFlags`, `getSelectedIndex`, `getString`, `insert`, `isSelected`, `set`, `setFitPolicy`, `setFont`, `setSelectedFlags`, `setSelectedIndex`, `size`. [hypothesis: port]
- `ScalingButton`: `destroy`, `doDraw`, `getHeight`, `getPivotX`, `getPivotY`, `getWidth`, `getX`, `getY`, `logicUpdate`, `pointerEventOccurred`, `resetState`, `setPosition`, `setSize`. [hypothesis: port]
- `ServerSocketMessage`: `getBytes`, `getSize`, `readBoolean`, `readByte`, `readInt`, `readShort`, `readUTF`, `readUnsignedByte`, `readUnsignedShort`, `skip`, `writeBoolean`, `writeByte`, `writeInt`, `writeShort`, `writeUTF`. [hypothesis: port]
- `Toolkit`: `getImage`, `keyEventWithChars`, `paint(GLGraphics)`. [hypothesis: port]
- `ServerBundle`: `percentageDataReceived`, `responseCodeReceived`. [hypothesis: port]
