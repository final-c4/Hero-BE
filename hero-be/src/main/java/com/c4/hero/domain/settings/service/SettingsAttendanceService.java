package com.c4.hero.domain.settings.service;

import com.c4.hero.domain.attendance.entity.WorkSystemTemplate;
import com.c4.hero.domain.attendance.entity.WorkSystemType;
import com.c4.hero.domain.settings.dto.request.WSTAttReqDTO;
import com.c4.hero.domain.settings.dto.response.WSTAttResDTO;
import com.c4.hero.domain.settings.mapper.SettingsMapper;
import com.c4.hero.domain.settings.repository.SettingAttTemplateRepository;
import com.c4.hero.domain.settings.repository.SettingAttTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingsAttendanceService {

    private final SettingsMapper settingsMapper;
    private final SettingAttTemplateRepository settingAttTemplateRepository;
    private final SettingAttTypeRepository settingAttTypeRepository;

    public List<WSTAttResDTO> getWorkSystemTemplates() {
        List<WSTAttResDTO> list = settingsMapper.selectWorkSystemTemplates();
        log.debug("WorkSystemTemplates count={}", (list == null ? 0 : list.size()));
        return list;
    }

    /**
     * 근무제 템플릿 업서트(INSERT/UPDATE)
     * - 신규: workSystemTemplateId == null -> INSERT
     * - 기존: workSystemTemplateId != null -> UPDATE
     */
    @Transactional // ✅ readOnly=false로 override
    public void upsertWorkSystemTemplates(List<WSTAttReqDTO> requestList) {
        if (requestList == null) {
            return;
        }

        // 1) 기본 유효성 검증
        for (WSTAttReqDTO dto : requestList) {
            if (dto.getWorkSystemTypeId() == null) {
                throw new IllegalArgumentException("workSystemTypeId는 필수입니다.");
            }
            if (dto.getStartTime() == null || dto.getEndTime() == null) {
                throw new IllegalArgumentException("startTime/endTime은 필수입니다.");
            }
            if (dto.getBreakMinMinutes() == null || dto.getBreakMinMinutes() < 0) {
                throw new IllegalArgumentException("breakMinMinutes는 0 이상이어야 합니다.");
            }
            if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
                throw new IllegalArgumentException("reason(근무제명/사유)은 필수입니다.");
            }
        }

        // 2) 한 번에 필요한 Type 로딩 (N+1 방지)
        Set<Integer> typeIds = requestList.stream()
                .map(WSTAttReqDTO::getWorkSystemTypeId)
                .collect(Collectors.toSet());

        Map<Integer, WorkSystemType> typeMap = settingAttTypeRepository.findAllById(typeIds).stream()
                .collect(Collectors.toMap(WorkSystemType::getWorkSystemTypeId, t -> t));

        // 존재하지 않는 타입이 있으면 에러
        for (Integer typeId : typeIds) {
            if (!typeMap.containsKey(typeId)) {
                throw new NoSuchElementException("존재하지 않는 workSystemTypeId=" + typeId);
            }
        }

        // 3) 기존 템플릿도 한 번에 로딩 (UPDATE 대상)
        List<Integer> existingIds = requestList.stream()
                .map(WSTAttReqDTO::getWorkSystemTemplateId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Integer, WorkSystemTemplate> existingTemplateMap = settingAttTemplateRepository.findAllById(existingIds).stream()
                .collect(Collectors.toMap(WorkSystemTemplate::getId, t -> t));

        // 4) INSERT/UPDATE 분기 처리
        List<WorkSystemTemplate> toSave = new ArrayList<>();

        for (WSTAttReqDTO dto : requestList) {
            WorkSystemType type = typeMap.get(dto.getWorkSystemTypeId());

            // (A) INSERT
            if (dto.getWorkSystemTemplateId() == null) {
                WorkSystemTemplate created = WorkSystemTemplate.create(
                        dto.getStartTime(),
                        dto.getEndTime(),
                        dto.getBreakMinMinutes(),
                        dto.getReason(),
                        type
                );
                toSave.add(created);
                continue;
            }

            // (B) UPDATE
            WorkSystemTemplate target = existingTemplateMap.get(dto.getWorkSystemTemplateId());
            if (target == null) {
                throw new NoSuchElementException("존재하지 않는 workSystemTemplateId=" + dto.getWorkSystemTemplateId());
            }

            target.update(
                    dto.getStartTime(),
                    dto.getEndTime(),
                    dto.getBreakMinMinutes(),
                    dto.getReason(),
                    type
            );

            // dirty checking으로도 업데이트 되지만,
            // saveAll에 같이 넣어도 무방(명시적 처리)
            toSave.add(target);
        }

        // 5) 저장
        settingAttTemplateRepository.saveAll(toSave);
    }
}
