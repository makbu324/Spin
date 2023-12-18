package com.spin_cake_con

// Mak - image uploader for temp online storage
// Currently unused after change in architecture
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
