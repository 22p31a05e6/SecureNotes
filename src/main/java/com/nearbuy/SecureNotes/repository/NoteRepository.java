package com.nearbuy.SecureNotes.repository;

import com.nearbuy.SecureNotes.document.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String> {

    List<Note> findByUserEmail(String userEmail);
}