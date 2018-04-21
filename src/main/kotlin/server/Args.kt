package server

open class Args {
    val allowedCorsOrigin: String = System.getenv("ALLOWED_CORS_ORIGIN") ?: "http://localhost"
}