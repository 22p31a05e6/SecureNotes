package com.nearbuy.SecureNotes.service;

import com.nearbuy.SecureNotes.document.Note;
import com.nearbuy.SecureNotes.dto.NoteRequest;
import com.nearbuy.SecureNotes.exception.ResourceNotFoundException;
import com.nearbuy.SecureNotes.exception.UnauthorizedActionException;
import com.nearbuy.SecureNotes.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note createNote(NoteRequest request, String userEmail) {

        Note note = new Note();

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setUserEmail(userEmail);

        return noteRepository.save(note);
    }

    public List<Note> getMyNotes(String userEmail) {

        return noteRepository.findByUserEmail(userEmail);
    }

    public Note updateNote(
            String noteId,
            NoteRequest request,
            String userEmail) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Note not found"));

        if (!note.getUserEmail().equals(userEmail)) {
            throw new UnauthorizedActionException(
                    "You are not allowed to modify this note"
            );
        }

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        return noteRepository.save(note);
    }

    public void deleteNote(
            String noteId,
            String userEmail) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Note not found"));

        if (!note.getUserEmail().equals(userEmail)) {
            throw new UnauthorizedActionException(
                    "You are not allowed to delete this note"
            );
        }

        noteRepository.delete(note);
    }
}