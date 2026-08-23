package com.samsepiol.library.token.management.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.samsepiol.library.core.security.credential.CredentialEnvelope;
import com.samsepiol.library.core.security.credential.CredentialRotationMetadata;
import com.samsepiol.library.repository.models.Entity;
import com.samsepiol.library.token.management.TokenReference;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Arrays;

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
public class TokenRecord extends Entity {
    private static final String ID_PREFIX = "TM";

    @NonNull @BsonProperty("namespace") String namespace;
    @NonNull @BsonProperty("subject") String subject;
    @NonNull @BsonProperty("name") String name;
    @BsonProperty("envelopeVersion") int envelopeVersion;
    @NonNull @BsonProperty("algorithm") String algorithm;
    @NonNull @BsonProperty("keyId") String keyId;
    @NonNull @BsonProperty("nonce") byte[] nonce;
    @NonNull @BsonProperty("ciphertext") byte[] ciphertext;
    @BsonProperty("encryptedAtEpochMillis") long encryptedAtEpochMillis;
    @BsonProperty("previousKeyId") String previousKeyId;
    @BsonProperty("rotatedAtEpochMillis") Long rotatedAtEpochMillis;

    public static TokenRecord from(TokenReference reference, CredentialEnvelope envelope) {
        var rotation = envelope.getRotationMetadata();
        return TokenRecord.builder()
                .id(reference.persistentId())
                .namespace(reference.namespace())
                .subject(reference.subject())
                .name(reference.name())
                .envelopeVersion(envelope.getEnvelopeVersion())
                .algorithm(envelope.getAlgorithm())
                .keyId(envelope.getKeyId())
                .nonce(envelope.getNonce())
                .ciphertext(envelope.getCiphertext())
                .encryptedAtEpochMillis(envelope.getEncryptedAtEpochMillis())
                .previousKeyId(rotation == null ? null : rotation.getPreviousKeyId())
                .rotatedAtEpochMillis(rotation == null ? null : rotation.getRotatedAtEpochMillis())
                .build();
    }

    @JsonIgnore
    public TokenReference reference() {
        return new TokenReference(namespace, subject, name);
    }

    @JsonIgnore
    public CredentialEnvelope envelope() {
        var rotation = previousKeyId == null ? null : new CredentialRotationMetadata(previousKeyId, rotatedAtEpochMillis);
        return new CredentialEnvelope(envelopeVersion, algorithm, keyId, nonce, ciphertext, encryptedAtEpochMillis, rotation);
    }

    public byte[] getNonce() {
        return Arrays.copyOf(nonce, nonce.length);
    }

    public byte[] getCiphertext() {
        return Arrays.copyOf(ciphertext, ciphertext.length);
    }

    @Override
    protected @NonNull String getIdPrefix() {
        return ID_PREFIX;
    }
}
