package com.spin_cake_con


class TransfershUploader : FormImageUploader() {
    override val formName: String
        get() = "filedata"
    override val serverURL: String
        get() = "https://transfer.sh"
    override val headerData: Map<String, String>
        get() = mapOf(
            "Max-Downloads" to "1",
            "Max-Days" to "1",
        )
}
