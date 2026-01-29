package com.rpsB.demo.controller;

import com.rpsB.demo.server.PythonGrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EchoController {

    private final PythonGrpcClient grpcClient;

    public EchoController(PythonGrpcClient grpcClient) {
        this.grpcClient = grpcClient;
    }

    @GetMapping("/echo")
    public String echo(@RequestParam String text) {
        return grpcClient.sendText(text);
    }
}
