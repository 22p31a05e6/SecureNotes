package com.nearbuy.SecureNotes.service;

import com.nearbuy.SecureNotes.document.Note;
import com.nearbuy.SecureNotes.dto.NoteRequest;
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
}