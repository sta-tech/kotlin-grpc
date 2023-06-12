fun main() {
    val server = AppGrpcServer(50051)
    server.start()
    server.blockUntilShutdown()
}