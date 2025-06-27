package com.keysersoze.yumyard.domain.usecase.draft

import javax.inject.Inject

data class DraftUseCases @Inject constructor(
    val getAllDraftsUseCase: GetAllDraftsUseCase,
    val saveDraftUseCase: SaveDraftUseCase,
    val deleteDraftUseCase: DeleteDraftUseCase,
    val getDraftByIdUseCase: GetDraftByIdUseCase,
    val deleteDraftByIdUseCase: DeleteDraftByIdUseCase,
    val upsertDraftUseCase: UpsertDraftUseCase
)
