package org.flinkcoin.mobile.demo.util;

import org.flinkcoin.crypto.CryptoException;
import org.flinkcoin.crypto.HashHelper;
import org.flinkcoin.crypto.KeyPair;
import org.flinkcoin.data.proto.common.Common;
import org.flinkcoin.helper.helpers.Base32Helper;
import com.google.protobuf.ByteString;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class BlockHelper {

    private static final int BLOCK_VERSION = 1;
    private static final byte[] EMPTY_BYTES = new byte[0];

    private static final byte[] DELEGATE_NODE_ID = Base32Helper.decode("3JEFYDDBOZGXNIL3Z2B3XSD3QI");

    public static String createCreateBlock(long timestamp, byte[] accountId, KeyPair keyPair) throws
            NoSuchAlgorithmException, CryptoException, InvalidKeyException,
            SignatureException {
        return Base32Helper.encode(createBlock(timestamp, Common.Block.BlockType.CREATE, EMPTY_BYTES, accountId, 0, 0, EMPTY_BYTES, EMPTY_BYTES, EMPTY_BYTES, keyPair).toByteArray());
    }

    public static Common.Block createReceiveBlock(long timestamp, byte[] previousBlockHash, byte[] accountId, long balance, long amount, byte[] sendAccountId,
                                                  byte[] receiveBlockHash, byte[] referenceCode, KeyPair keyPair) throws NoSuchAlgorithmException, CryptoException, InvalidKeyException,
            SignatureException {
        return createBlock(timestamp, Common.Block.BlockType.RECEIVE, previousBlockHash, accountId, balance, amount, sendAccountId, receiveBlockHash, referenceCode, keyPair);
    }

    public static Common.Block createSendBlock(long timestamp, byte[] previousBlockHash, byte[] accountId, long balance, long amount, byte[] sendAccountId,
                                               byte[] referenceCode, KeyPair keyPair) throws NoSuchAlgorithmException, CryptoException, InvalidKeyException,
            SignatureException {
        return createBlock(timestamp, Common.Block.BlockType.SEND, previousBlockHash, accountId, balance, amount, sendAccountId, EMPTY_BYTES, referenceCode, keyPair);
    }

    public static Common.PaymentRequest createPaymentRequest(byte[] fromAccount, byte[] toAccount, long amount, byte[] referenceCode) {
        return Common.PaymentRequest.newBuilder()
                .setFromAccountId(ByteString.copyFrom(fromAccount))
                .setToAccountId(ByteString.copyFrom(toAccount))
                .setAmount(amount)
                .setReferenceCode(ByteString.copyFrom(referenceCode))
                .build();
    }

    private static Common.Block createBlock(long timestamp, Common.Block.BlockType blockType, byte[] previousBlockHash, byte[] accountId, long balance, long amount,
                                            byte[] sendAccountId, byte[] receiveBlockHash, byte[] referenceCode, KeyPair keyPair) throws CryptoException,
            NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        Common.Block.Body.Builder bodyBuilder = Common.Block.Body.newBuilder();
        bodyBuilder.setVersion(BLOCK_VERSION);
        bodyBuilder.setTimestamp(timestamp);
        bodyBuilder.setBlockType(blockType);
        if (previousBlockHash.length > 0) {
            bodyBuilder.setPreviousBlockHash(ByteString.copyFrom(previousBlockHash));
        }
        bodyBuilder.setAccountId(ByteString.copyFrom(accountId));
        bodyBuilder.setDelegatedNodeId(ByteString.copyFrom(DELEGATE_NODE_ID));
        bodyBuilder.setBalance(balance);
        bodyBuilder.setAmount(amount);
        if (sendAccountId.length > 0) {
            bodyBuilder.setSendAccountId(ByteString.copyFrom(sendAccountId));
        }
        if (receiveBlockHash.length > 0) {
            bodyBuilder.setReceiveBlockHash(ByteString.copyFrom(receiveBlockHash));
        }
        if (referenceCode.length > 0) {
            bodyBuilder.setReferenceCode(ByteString.copyFrom(referenceCode));
        }
        bodyBuilder.setPublicKeys(Common.Block.PublicKeys.newBuilder()
                .addPublicKey(ByteString.copyFrom(keyPair.getPublicKey().getPublicKey())));

        Common.Block.Body body = bodyBuilder.build();

        ByteString blockHash = ByteString.copyFrom(HashHelper.sha512(body.toByteArray()));

        Common.Block.Builder blockBuilder = Common.Block.newBuilder()
                .setBody(body)
                .setSignatues(Common.Block.Signatures.newBuilder()
                        .addSignature(ByteString.copyFrom(signData(keyPair, body.toByteArray())))
                )
                .setBlockHash(Common.Block.Hash.newBuilder().setHash(blockHash));

        return blockBuilder.build();
    }

    private static byte[] signData(KeyPair keyPair, byte[] data) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        EdDSANamedCurveSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        EdDSAEngine edDSAEngine = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));

        edDSAEngine.initSign(keyPair.getPrivateKey().getEdDSAPrivateKey());
        edDSAEngine.update(data);
        return edDSAEngine.sign();
    }
}
