package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MedicalServiceImplTest {

    @Test
    void checkBloodPressure_whenPressureNotNormal_shouldSendAlert() {
        // Arrange
        String patientId = "1";
        BloodPressure abnormalPressure = new BloodPressure(140, 90);
        BloodPressure normalPressure = new BloodPressure(120, 80);

        PatientInfo patientInfo = new PatientInfo(
                patientId,
                "Иван",
                "Иванов",
                LocalDate.of(1980, 1, 1),
                new HealthInfo(new BigDecimal("36.6"), normalPressure)
        );

        PatientInfoRepository patientInfoRepository = mock(PatientInfoRepository.class);
        when(patientInfoRepository.getById(patientId)).thenReturn(patientInfo);

        SendAlertService alertService = mock(SendAlertService.class);
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        medicalService.checkBloodPressure(patientId, abnormalPressure);

        // Assert
        verify(alertService).send(messageCaptor.capture());
        assertEquals(String.format("Warning, patient with id: %s, need help", patientId), messageCaptor.getValue());
    }

    @Test
    void checkTemperature_whenTemperatureNotNormal_shouldSendAlert() {
        // Arrange
        String patientId = "1";
        BigDecimal abnormalTemperature = new BigDecimal("35.0"); // Изменено значение
        BigDecimal normalTemperatureValue = new BigDecimal("36.6");

        PatientInfo patientInfo = new PatientInfo(
                patientId,
                "Иван",
                "Иванов",
                LocalDate.of(1980, 1, 1),
                new HealthInfo(normalTemperatureValue, new BloodPressure(120, 80))
        );

        PatientInfoRepository patientInfoRepository = mock(PatientInfoRepository.class);
        when(patientInfoRepository.getById(patientId)).thenReturn(patientInfo);

        SendAlertService alertService = mock(SendAlertService.class);
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        medicalService.checkTemperature(patientId, abnormalTemperature);

        // Assert
        verify(alertService).send(messageCaptor.capture());
        assertEquals(String.format("Warning, patient with id: %s, need help", patientId), messageCaptor.getValue());
    }

    @Test
    void checkBloodPressure_whenPressureNormal_shouldNotSendAlert() {
        // Arrange
        String patientId = "1";
        BloodPressure normalPressure = new BloodPressure(120, 80);

        PatientInfo patientInfo = new PatientInfo(
                patientId,
                "Иван",
                "Иванов",
                LocalDate.of(1980, 1, 1),
                new HealthInfo(new BigDecimal("36.6"), normalPressure)
        );

        PatientInfoRepository patientInfoRepository = mock(PatientInfoRepository.class);
        when(patientInfoRepository.getById(patientId)).thenReturn(patientInfo);

        SendAlertService alertService = mock(SendAlertService.class);
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);

        // Act
        medicalService.checkBloodPressure(patientId, normalPressure);

        // Assert
        verify(alertService, never()).send(anyString());
    }

    @Test
    void checkTemperature_whenTemperatureNormal_shouldNotSendAlert() {
        // Arrange
        String patientId = "1";
        BigDecimal normalTemperatureValue = new BigDecimal("36.6");

        PatientInfo patientInfo = new PatientInfo(
                patientId,
                "Иван",
                "Иванов",
                LocalDate.of(1980, 1, 1),
                new HealthInfo(normalTemperatureValue, new BloodPressure(120, 80))
        );

        PatientInfoRepository patientInfoRepository = mock(PatientInfoRepository.class);
        when(patientInfoRepository.getById(patientId)).thenReturn(patientInfo);

        SendAlertService alertService = mock(SendAlertService.class);
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);

        BigDecimal temperature = new BigDecimal("37.0");

        // Act
        medicalService.checkTemperature(patientId, temperature);

        // Assert
        verify(alertService, never()).send(anyString());
    }
}