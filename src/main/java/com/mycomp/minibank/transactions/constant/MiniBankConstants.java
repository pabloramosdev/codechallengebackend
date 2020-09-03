package com.mycomp.minibank.transactions.constant;

import java.math.BigDecimal;

public class MiniBankConstants {

    public static final BigDecimal MAX_BALANCE_PERMITTED = BigDecimal.valueOf(999_999_999.99)
            .setScale(2, BigDecimal.ROUND_HALF_UP);

    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);

    public static final BigDecimal ONE = BigDecimal.ONE.setScale(2, BigDecimal.ROUND_HALF_UP);

    public static final BigDecimal CENT = BigDecimal.valueOf(0.01).setScale(2, BigDecimal.ROUND_HALF_UP);

    public static final String TRANSACTION_CODE = "A";

    public static final String REGEX_REFERENCE = "^A[0-9]{9}$";
    public static final String REGEX_ACCOUNT_IBAN = "^ES98[0-9]{20}$";
    public static final String REGEX_CHANNEL = "^CLIENT|ATM|INTERNAL$";
    public static final String REGEX_SORT_TYPE = "^ASC|DESC$";

}
