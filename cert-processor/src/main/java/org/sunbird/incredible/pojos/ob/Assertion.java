package org.sunbird.incredible.pojos.ob;


import org.apache.commons.lang3.StringUtils;
import org.sunbird.incredible.pojos.CompositeIdentityObject;
import org.sunbird.incredible.pojos.ob.exeptions.InvalidDateFormatException;
import org.sunbird.incredible.pojos.ob.valuator.ExpiryDateValuator;
import org.sunbird.incredible.pojos.ob.valuator.IssuedDateValuator;


/**
 * Exactly per OpenBadges v2 specification
 */
public class Assertion extends OBBase {
    /**
     * HTTP URL or UUID from urn:uuid namespace
     */
    private String id;

    /**
     * Simple string "Assertion" or URLs or IRIs of current context
     */
    private String[] type;
    /**
     * DateTime string compatible with ISO 8601 guideline
     * For example, 2016-12-31T23:59:59+00:00
     */
    private String issuedOn;
    private CompositeIdentityObject recipient;
    private BadgeClass badge;

    /**
     * IRI or document representing an image representing this user's achievement.
     * This must be a PNG or SVG image. Otherwise, use BadgeClass member.
     */
    private String image;
    private Evidence evidence;
    /**
     * DateTime string compatible with ISO 8601 guideline
     * For example, 2016-12-31T23:59:59+00:00
     */
    private String expires;

    private VerificationObject verification;

    /**
     * A narrative that connects multiple pieces of evidence
     */
    private String narrative;

    // Part of v2 spec, not referred in inCredible
    /**
     * Defaults to false if Assertion is not referenced from a
     * revokedAssertions list and may be omitted.
     */
    private boolean revoked = false;

    /**
     * Optional published reason for revocation, if revoked.
     */
    private String revocationReason;

    private String location;

    private String studentRegNo;

    private String certificateNum;

    private String implication;

    private String recipientEmail;

    private String logoImage1;

    private String logoImage2;

    private String certify;

    private String textBeforeCourse;

    private String textAfterCourse;

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTextAfterCourse() {
        return textAfterCourse;
    }

    public void setTextAfterCourse(String textAfterCourse) {
        this.textAfterCourse = textAfterCourse;
    }

    public String getTextBeforeCourse() {
        return textBeforeCourse;
    }

    public void setTextBeforeCourse(String textBeforeCourse) {
        this.textBeforeCourse = textBeforeCourse;
    }

    public String getCertify() {
        return certify;
    }

    public void setCertify(String certify) {
        this.certify = certify;
    }

    public Assertion() {
    }

    public Assertion(String ctx) {
        setContext(ctx);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public String getCertificateNum() {
        return certificateNum;
    }

    public void setCertificateNum(String certificateNum) {
        this.certificateNum = certificateNum;
    }

    public String getImplication() {
        return implication;
    }

    public void setImplication(String implication) {
        this.implication = implication;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public String getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(String issuedOn) throws InvalidDateFormatException {

        IssuedDateValuator issuedDateValuator = new IssuedDateValuator();
        if (issuedDateValuator.evaluates(issuedOn) == null) {
            throw new InvalidDateFormatException("Issued date is not in a given format");
        } else {
            this.issuedOn = issuedDateValuator.evaluates(issuedOn);
        }

    }

    public CompositeIdentityObject getRecipient() {
        return recipient;
    }

    public void setRecipient(CompositeIdentityObject recipient) {
        this.recipient = recipient;
    }

    public BadgeClass getBadge() {
        return badge;
    }

    public void setBadge(BadgeClass badge) {
        this.badge = badge;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) throws InvalidDateFormatException {
        if (StringUtils.isNotBlank(expires)) {
            ExpiryDateValuator valuator = new ExpiryDateValuator(this.getIssuedOn());
            if (valuator.evaluates(expires) == null) {
                throw new InvalidDateFormatException("Expiry date is in wrong format");
            } else {
                this.expires = valuator.evaluates(expires);
            }
        }

    }

    public VerificationObject getVerification() {
        return verification;
    }

    public void setVerification(VerificationObject verification) {
        this.verification = verification;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public String getRevocationReason() {
        return revocationReason;
    }

    public void setRevocationReason(String revocationReason) {
        this.revocationReason = revocationReason;
    }

    public String getLogoImage1() {
        return logoImage1;
    }

    public void setLogoImage1(String logoImage1) {
        this.logoImage1 = logoImage1;
    }

    public String getLogoImage2() {
        return logoImage2;
    }

    public void setLogoImage2(String logoImage2) {
        this.logoImage2 = logoImage2;
    }
}
