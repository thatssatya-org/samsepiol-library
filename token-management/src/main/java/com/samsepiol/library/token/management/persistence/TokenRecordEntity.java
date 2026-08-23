package com.samsepiol.library.token.management.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.samsepiol.library.encryption.credential.CredentialEnvelope;
import com.samsepiol.library.encryption.credential.CredentialRotationMetadata;
import com.samsepiol.library.repository.models.Entity;
import com.samsepiol.library.token.management.TokenReference;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Base64;
import java.util.Objects;

/**
 * Mongo-native representation of a {@link CredentialEnvelope}. It deliberately flattens the envelope so the
 * credential core remains storage-agnostic and does not acquire a Mongo driver dependency.
 */
@Value
@SuperBuilder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(onConstructor_ = {@BsonCreator})
public class TokenRecordEntity extends Entity {
    private static final String ID_PREFIX = "TM";

    @NonNull @BsonProperty("namespace") String namespace;
    @NonNull @BsonProperty("subject") String subject;
    @NonNull @BsonProperty("name") String name;
    @BsonProperty("envelopeVersion") Integer envelopeVersion;
    @NonNull @BsonProperty("algorithm") String algorithm;
    @NonNull @BsonProperty("keyId") String keyId;
    @NonNull @BsonProperty("nonce") String nonceBase64;
    @NonNull @BsonProperty("ciphertext") String ciphertextBase64;
    @BsonProperty("encryptedAtEpochMillis") Long encryptedAtEpochMillis;
    @BsonProperty("previousKeyId") String previousKeyId;
    @BsonProperty("rotatedAtEpochMillis") Long rotatedAtEpochMillis;

    public static TokenRecordEntity from(TokenReference reference, CredentialEnvelope envelope) {
        var rotation = envelope.getRotationMetadata();
        return TokenRecordEntity.builder()
                .id(reference.persistentId())
                .namespace(reference.namespace())
                .subject(reference.subject())
                .name(reference.name())
                .envelopeVersion(envelope.getEnvelopeVersion())
                .algorithm(envelope.getAlgorithm())
                .keyId(envelope.getKeyId())
                .nonceBase64(Base64.getEncoder().encodeToString(envelope.getNonce()))
                .ciphertextBase64(Base64.getEncoder().encodeToString(envelope.getCiphertext()))
                .encryptedAtEpochMillis(envelope.getEncryptedAtEpochMillis())
                .previousKeyId(Objects.isNull(rotation) ? null : rotation.getPreviousKeyId())
                .rotatedAtEpochMillis(Objects.isNull(rotation) ? null : rotation.getRotatedAtEpochMillis())
                .build();
    }

    @JsonIgnore
    public TokenReference reference() {
        return TokenReference.builder().namespace(namespace).subject(subject).name(name).build();
    }

    @JsonIgnore
    public CredentialEnvelope envelope() {
        var rotation = Objects.isNull(previousKeyId) ? null : CredentialRotationMetadata.builder()
                .previousKeyId(previousKeyId).rotatedAtEpochMillis(rotatedAtEpochMillis).build();
        return CredentialEnvelope.builder().envelopeVersion(envelopeVersion).algorithm(algorithm).keyId(keyId)
                .nonce(Base64.getDecoder().decode(nonceBase64)).ciphertext(Base64.getDecoder().decode(ciphertextBase64))
                .encryptedAtEpochMillis(encryptedAtEpochMillis).rotationMetadata(rotation).build();
    }

    @Override
    protected @NonNull String getIdPrefix() {
        return ID_PREFIX;
    }
}
