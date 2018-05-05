package server

import java.net.URL

open class Args {
    val allowedCorsOrigin: String = System.getenv("ALLOWED_CORS_ORIGIN") ?: "http://localhost"
    val apiUrl: URL = URL(System.getenv("API_URL") ?: throw Exception("Missing environment variable: API_URL"))
    val secureApiConnection: Boolean = try { System.getenv("SECURE_API_CONNECTION")!!.toBoolean() } catch (e: Exception) { true }
}