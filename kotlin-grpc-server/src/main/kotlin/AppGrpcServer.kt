import io.grpc.Server
import io.grpc.ServerBuilder

class AppGrpcServer(private val port: Int) {
    val server: Server = ServerBuilder
        .forPort(port)
        .addService(FileService())
        .build()

    fun start() {
        server.start()
        println("gRPC server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("shutting down gRPC server")
                this@AppGrpcServer.stop()
                println("gRPC server shut down")
            }
        )
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private fun stop() {
        server.shutdown()
    }
}