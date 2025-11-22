package com.jacobferrell.chess.service.game.piece;

import com.jacobferrell.chess.model.PieceEntity;
import com.jacobferrell.chess.repository.PieceEntityRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PieceService {

    private final PieceEntityRepository repository;

    @PostConstruct
    private void loadCache() {
        PieceEntity.preload(repository.findAll());
    }

}
