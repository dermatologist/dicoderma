package in.co.dermatologist.dicoderma;

import lombok.Data;

/**
 * DicomSCModel
 * 
 * @author Bell Eapen
 */
@Data
public class DicomSCModel {
    private String PatientName; // "Simson^Homer"
    private String PatientSex; // "M" OR "F"
    private String PatientID;
    private String IssuerOfPatientID;
    private String TypeOfPatientID; // "RFID"

    private String StudyDate; // "20110404" OR "20110404-20110405"
    private String StudyTime; // "15" OR "15-20"
    private Object StudyDescription;

}