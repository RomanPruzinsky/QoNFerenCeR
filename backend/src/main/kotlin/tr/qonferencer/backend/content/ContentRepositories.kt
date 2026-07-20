package tr.qonferencer.backend.content

import org.springframework.data.jpa.repository.JpaRepository

interface LanguageRepository : JpaRepository<Language, String>

interface TranslationRepository : JpaRepository<Translation, TranslationId>

interface CustomScreenRepository : JpaRepository<CustomScreen, String>
