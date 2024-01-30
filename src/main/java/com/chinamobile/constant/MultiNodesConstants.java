package com.chinamobile.constant;

/**
 * @description: 多節點常量類
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/22 12:08
 */
public class MultiNodesConstants {

    //多維數組定義產生路經下標
    public static final int[][][][] RECURSIVE_ARRAY = new int[3][3][4][4];

    //多維數組定義產生路經下標，第一維代表開頭下標維度，第二維代表中間路經，第三位代表Node路經
    public static final int[][][] SUBSCRIPT_ARRAY = new int[3][3][4];

    //$.["pattern-action-union"][0].pattern["condition-selection-pattern"]["condition-node"][0]["sub-pattern-and-action"]["pattern-action-union"][0]
    //$.["pattern-action-union"][0].pattern["condition-selection-pattern"]["condition-node"][0]["sub-pattern-and-action"]["pattern-action-union"][0]
    // .pattern["condition-selection-pattern"]["condition-node"][1]["sub-pattern-and-action"]["pattern-action-union"][0]

    //這是樹狀圖的開頭基礎路經，格式基本固定。
    //結尾需要加上[數組下標]
    public static String BASIC_NODE_PATH_START = "$.[\"pattern-action-union\"]";

    //.pattern["condition-selection-pattern"]["condition-node"]

    //這是樹狀圖的中間路經，格式基本固定。
    public static String BASIC_NODE_PATH_MID = "[\"sub-pattern-and-action\"][\"pattern-action-union\"]";

    public static String NODES_PATH = ".pattern[\"condition-selection-pattern\"][\"condition-node\"]";


    // action動作的基礎路經
    public static final String ACTION_BASIC_PATH = "[\"sub-pattern-and-action\"][\"pattern-action-union\"][0].";

    // 动作类别，如 charge-discount-action 等动作
    public static final String ACTION_TYPE_PATH = "[\"sub-pattern-and-action\"][\"pattern-action-union\"][0].action[\"@attributes\"][\"chosen-element\"]";

    // <!-- discount-fee-flag = 1 --> 作用未知
    public static final String DISCOUNT_FEE_FLAG_PATH = "[\"discount-fee-flag\"].value";

    // <!-- discount-prorate-method = 1 --> 作用未知
    public static final String DISCOUNT_PRORATE_METHOD_PATH = "[\"discount-prorate-method\"].value";

    // <!-- integer = 0 -->，用來獲取資費信息的路經
    public static final String INTEGER_PATH = "[\"discount-calc\"][\"fixed-value\"].constant[\"constant-value\"].integer.value";

    // <!-- exponent = 0 --> 作用未知
    public static final String EXPONENT_PATH = "[\"discount-calc\"][\"fixed-value\"].constant[\"constant-value\"].exponent.value";

    // <!-- currency-measurement = 501 --> 作用未知
    public static final String CURRENCY_MEASUREMENT_PATH = "[\"discount-calc\"][\"fixed-value\"][\"currency-measurement\"].value";


}
