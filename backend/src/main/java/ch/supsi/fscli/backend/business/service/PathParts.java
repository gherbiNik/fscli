package ch.supsi.fscli.backend.business.service;

import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;

public record PathParts(DirectoryNode parentDir, String name) {}
