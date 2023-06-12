import io.grpc.ManagedChannelBuilder
import kotlin.io.path.Path
import kotlin.io.path.exists

suspend fun main(args: Array<String>) {
    val filePath = args.firstOrNull()
    if (filePath == null || !Path(filePath).exists()) {
        println("Invalid path")
        return
    }
    println("Uploading $filePath")
    val contentType = args.getOrElse(1, { _ -> "application/octet-stream" })
    val channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build()
    FileServiceClient(channel).use {
        val result = it.uploadFile(filePath, contentType)
        println("Upload completed! Result: ${result.status}")
    }
}