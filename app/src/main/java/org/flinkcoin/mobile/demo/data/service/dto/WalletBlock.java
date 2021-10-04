package org.flinkcoin.mobile.demo.data.service.dto;

import org.flinkcoin.data.proto.common.Common;
import org.flinkcoin.helper.helpers.Base32Helper;

public class WalletBlock {

    public String accountId;
    public Long timestamp;
    public Long balance;
    public Long amount;
    public String hash;
    public String previousHash;
    public String sendAccountId;
    public String receiveBlockHash;
    public Common.Block.BlockType blockType;
    public String referenceCode;

    public WalletBlock(Common.Block block) {
        this.accountId = Base32Helper.encode(block.getBody().getAccountId().toByteArray());
        this.timestamp = block.getBody().getTimestamp();
        this.balance = block.getBody().getBalance();
        this.amount = block.getBody().getAmount();
        this.hash = Base32Helper.encode(block.getBlockHash().getHash().toByteArray());
        this.previousHash = Base32Helper.encode(block.getBody().getPreviousBlockHash().toByteArray());
        this.sendAccountId = Base32Helper.encode(block.getBody().getSendAccountId().toByteArray());
        this.blockType = block.getBody().getBlockType();
        this.sendAccountId = Base32Helper.encode(block.getBody().getSendAccountId().toByteArray());
        this.referenceCode = block.getBody().getReferenceCode().toStringUtf8();
        this.receiveBlockHash = Base32Helper.encode(block.getBody().getReceiveBlockHash().toByteArray());
    }

}
