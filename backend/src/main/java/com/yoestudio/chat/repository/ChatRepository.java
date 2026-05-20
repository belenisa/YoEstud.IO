package com.yoestudio.chat.repository;

import com.yoestudio.chat.model.MensajesChat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<MensajesChat, String> {
    Optional<MensajesChat> findBySesionId(String sesionId);
    List<MensajesChat> findByUsuarioId(Long usuarioId);
    void deleteBySesionId(String sesionId);
}
