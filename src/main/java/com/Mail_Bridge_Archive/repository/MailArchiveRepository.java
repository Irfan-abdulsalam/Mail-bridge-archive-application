package com.Mail_Bridge_Archive.repository;

import com.Mail_Bridge_Archive.model.MailArchive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailArchiveRepository extends JpaRepository<MailArchive, Long> {
}
