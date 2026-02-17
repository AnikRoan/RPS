package com.rpsB.demo.controller;

import com.rpsB.demo.dto.VoteRequest;
import com.rpsB.demo.dto.VoteResponse;
import com.rpsB.demo.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/{recipe_id}")
    public ResponseEntity<VoteResponse> createVote(@PathVariable("recipe_id") UUID recipe_id,
                                                   @RequestBody VoteRequest request) {
        return ResponseEntity.ok().body(voteService.createVote(request, recipe_id));
    }

    @PutMapping("/{recipe_id}/votes/{vote_id}")
    public ResponseEntity<VoteResponse> updateVote(@PathVariable("recipe_id") UUID recipe_id,
                                                   @PathVariable("vote_id")Long vote_id,
                                                   @RequestBody VoteRequest request) {
        return ResponseEntity.ok().body(voteService.updateVote(request, recipe_id, vote_id));
    }

    @GetMapping("/{recipe_id}")
    public ResponseEntity<Page<VoteResponse>> getVotes(@PathVariable("recipe_id") UUID recipe_id,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(voteService.getVotes(recipe_id, page, size));
    }

    @DeleteMapping("/{vote_id}")
    public ResponseEntity<String> deliteVote(@PathVariable("vote_id") Long vote_id) {
        return ResponseEntity.ok().body(voteService.deleteVote(vote_id));
    }
}
