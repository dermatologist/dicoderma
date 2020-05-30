package in.co.dermatologist.dicoderma;

import lombok.Data;

/**
 * DicomSCModel
 * 
 * @author Bell Eapen
 */
@Data
public class DicomSCModel {
    public String PatientName; // "Simson^Homer"
    public GenderEnum PatientSex; // "M" OR "F"
    public String PatientID;
    public String IssuerOfPatientID;
    public String TypeOfPatientID; // "RFID"

    public String StudyDate; // "20110404" OR "20110404-20110405"
    public String StudyTime; // "15" OR "15-20"
    public Object StudyDescription;

}