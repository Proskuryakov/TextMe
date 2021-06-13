package ru.vsu.cs.textme.backend.db.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChatStatus {
    STATUS_MEMBER(0),
    STATUS_LEAVE(1),
    STATUS_KICK(2);
    private final int id;
}
