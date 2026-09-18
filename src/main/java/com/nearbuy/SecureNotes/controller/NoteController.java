package com.nearbuy.SecureNotes.controller;

import com.nearbuy.SecureNotes.document.Note;
import com.nearbuy.SecureNotes.dto.NoteRequest;
import com.nearbuy.SecureNotes.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<Note> createNote(
            @Valid @RequestBody NoteRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        Note note = noteService.createNote(request, email);

        return ResponseEntity.ok(note);
    }

    @GetMapping
    public ResponseEntity<List<Note>> getMyNotes(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                noteService.getMyNotes(email)
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(
            @PathVariable String id,
            @Valid @RequestBody NoteRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        Note updatedNote =
                noteService.updateNote(id, request, email);

        return ResponseEntity.ok(updatedNote);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable String id,
            Authentication authentication) {

        String email = authentication.getName();

        noteService.deleteNote(id, email);

        return ResponseEntity.noContent().build();
    }
}