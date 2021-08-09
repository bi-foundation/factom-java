package org.blockchain_innovation.factom.client.api.ops;

import org.blockchain_innovation.factom.client.api.model.Transaction;

import javax.inject.Named;
import javax.inject.Singleton;
import java.nio.ByteBuffer;

@Named
@Singleton
public class TransactionOperations {

    //    create(List<TransactionResponse.Transaction.Input>)


    /**
     * @return 6 byte current milli timestamp
     */
    public byte[] currentTimeMillis() {
        return toMilliTimestamp(System.currentTimeMillis());
    }

    /**
     * @return 6 byte milli timestamp
     */
    public byte[] toMilliTimestamp(long timestamp) {
        byte[] holder = ByteBuffer.allocate(8).putLong(timestamp).array();
        return new byte[]{holder[2], holder[3], holder[4], holder[5], holder[6], holder[7]};
    }


    public void calculateFees(Transaction transaction) {

    }

    public void calculateFees(Transaction.Builder transactionBuilder) {

    }
}
