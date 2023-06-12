import com.google.protobuf.ByteString
import io.grpc.ManagedChannel
import kotlinx.coroutines.flow.flow
import ru.statech.*
import java.io.Closeable
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path

class FileServiceClient(private val channel: ManagedChannel): Closeable {
    private val stub: FileServiceGrpcKt.FileServiceCoroutineStub
            = FileServiceGrpcKt.FileServiceCoroutineStub(channel);

    suspend fun uploadFile(path: String, contentType: String): FileUploadResponse {
        val inputStream = File(path).inputStream()
        val bytes = ByteArray(4096)
        var size: Int
        return stub.upload(flow {
            // First, send file metadata
            val metaRequest = FileUploadRequest.newBuilder()
                .setMeta(
                    FileMeta.newBuilder()
                    .setName(Path(path).fileName.toString())
                    .setContentType(contentType))
                .build()
            emit(metaRequest)

            // Next, upload file content
            while (inputStream.read(bytes).also { size = it } > 0) {
                val uploadRequest: FileUploadRequest = FileUploadRequest.newBuilder()
                    .setContent(
                        FileContent.newBuilder()
                        .setData(ByteString.copyFrom(bytes, 0, size)))
                    .build()
                emit(uploadRequest)
            }
        })
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}