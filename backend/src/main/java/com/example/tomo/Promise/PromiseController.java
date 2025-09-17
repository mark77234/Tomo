package com.example.tomo.Promise;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PromiseController {

    private final PromiseService promiseService;

    public PromiseController(PromiseService promiseService) {
        this.promiseService = promiseService;
    }

    @PostMapping("/promises")
    public Long addPromise(@RequestBody addPromiseRequestDTO dto) {
        return promiseService.addPromise(dto);

    }

    @GetMapping("/promises/{promise_id}")
    public ResponseGetPromiseDto getPromise(@PathVariable Long promise_id){
        return promiseService.getPromise(promise_id);
    }

    @GetMapping("/promises")
    public List<ResponseGetPromiseDto> getAllPromises(@RequestParam Long moim_id){
        return promiseService.getAllPromise(moim_id);

    }
}
