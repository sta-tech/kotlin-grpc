import kotlinx.coroutines.flow.Flow
import ru.statech.FileServiceGrpcKt
import ru.statech.FileUploadRequest
import ru.statech.FileUploadResponse
import ru.statech.FileUploadStatus
import java.io.FileOutputStream
import java.io.OutputStream

internal class FileService: FileServiceGrpcKt.FileServiceCoroutineImplBase() {
    private val _storageDirectory: String = "/tmp/"

    override suspend fun upload(requests: Flow<FileUploadRequest>): FileUploadResponse {
        var writer: OutputStream? = null
        var status: FileUploadStatus;

        try {
            println("Receiving a file...")
            requests.collect { request ->
                if (request.hasMeta()) {
                    writer = openFileWriter(request.meta.name)
                } else {
                    writer?.write(request.content.toByteArray())
                    writer?.flush()
                }
            }
            writer?.close()
            println("File saved!")
            status = FileUploadStatus.Success;
        }
        catch (err: Exception) {
            println("Error occured during saving a file: ${err.message}")
            status = FileUploadStatus.Failed;
        }

        return FileUploadResponse.newBuilder()
            .setStatus(status)
            .build();
    }

    private fun openFileWriter(fileName: String) =
        FileOutputStream(_storageDirectory + fileName)
}