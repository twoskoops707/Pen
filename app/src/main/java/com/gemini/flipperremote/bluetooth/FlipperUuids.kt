package com.gemini.flipperremote.bluetooth

import java.util.UUID

object FlipperUuids {
    val SERVICE_UUID: UUID = UUID.fromString("0000fe60-cc7a-482a-984a-7f2ed5b3e58f")
    val TX_CHAR_UUID: UUID = UUID.fromString("0000fe61-8e22-4541-9d4c-21edae82ed19")
    val RX_CHAR_UUID: UUID = UUID.fromString("0000fe62-8e22-4541-9d4c-21edae82ed19")
}
