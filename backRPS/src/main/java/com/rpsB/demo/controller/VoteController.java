package com.rpsB.demo.controller;

import com.rpsB.demo.dto.VoteRequest;
import com.rpsB.demo.dto.VoteResponse;
import com.rpsB.demo.security.oauth2.CurrentUserProvider;
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
@RequestMapping("/recipes/{recipeId}/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final CurrentUserProvider userProvider;

    @PostMapping()
    public ResponseEntity<VoteResponse> createVote(@PathVariable UUID recipeId,
                                                   @RequestBody VoteRequest request) {
        return ResponseEntity.ok().body(voteService.createVote(
                request,
                recipeId,
                userProvider.getAuthUserPrincipalId()));
    }

    @PutMapping("/{voteId}")
    public ResponseEntity<VoteResponse> updateVote(@PathVariable UUID recipeId,
                                                   @PathVariable Long voteId,
                                                   @RequestBody VoteRequest request) {
        return ResponseEntity.ok().body(voteService.updateVote(
                request,
                recipeId,
                voteId,
                userProvider.getAuthUserPrincipalId()));
    }

    @GetMapping()
    public ResponseEntity<Page<VoteResponse>> getVotes(@PathVariable UUID recipeId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(voteService.getVotes(recipeId, page, size));
    }

    @DeleteMapping("/{voteId}")
    public ResponseEntity<String> deliteVote(@PathVariable UUID recipeId,
                                             @PathVariable Long voteId) {
        return ResponseEntity.ok().body(voteService.deleteVote(
                recipeId,
                voteId,
                userProvider.getAuthUserPrincipalId()));
    }
}
