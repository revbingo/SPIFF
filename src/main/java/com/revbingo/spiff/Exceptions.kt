package com.revbingo.spiff

class ExecutionException(msg: String, e: Throwable?) : RuntimeException(msg, e) {
    constructor(msg: String) : this(msg, null) { }
}

class AdfFormatException(msg: String, e: Throwable?) : RuntimeException(msg, e) {
    constructor(msg: String) : this(msg, null) { }
}
