package com.chinamobile.constant;

/**
 * @description: 單節點路經常量類，只需要修改OFFERING_ID來獲取EXCEL表格
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/12 16:07
 */
public class SingleNodePathConstants {

    //OFFERING_ID
    public static String OFFERING_ID = "51001800";
    //存儲和讀取文件夾基本路經
    public static String BASIC_PATH = "C:\\Users\\P7587\\Desktop\\testFile\\";
    //從華為數據庫複製的信息放在TXT文件夾中
    public static String RULE_TEXT_FILE = "_PLAN_POLICY_RULE.txt";
    //xml文件存儲名稱
    public static String RULE_XML_FILE = "_PLAN_POLICY_RULE.xml";
    //xml文件轉碼後的<code>標籤存儲名稱
    public static String RULE_XML_FILER_FILE = "_PLAN_POLICY_RULE_FILTER.xml";
    //JSON文件存儲名稱
    public static String RULE_JSON_PATH = "_PLAN_POLICY_RULE.json";

    //這是樹狀圖的開頭基礎路經，格式基本固定。
    //結尾需要加上[數組下標]
    public static String BASIC_NODE_PATH_START = "$.[\"pattern-action-union\"].pattern[\"condition-selection-pattern\"][\"condition-node\"]";

    //結尾需要加上[數組下標]
    //如果判斷為非葉子節點則進行增加該路經
    //不加下標則是獲取conditionNodeList，可獲取數組大小和數組數據
    //這是樹狀圖的中間路經，格式基本固定。
    public static String BASIC_NODE_PATH_MID = "[\"sub-pattern-and-action\"][\"pattern-action-union\"].pattern[\"condition-selection-pattern\"][\"condition-node\"]";

    //其實和BASIC_NODE_PATH_MID路經一樣，只是方便作區分。
    public static String CONDITION_NODE_LIST_PATH = "[\"sub-pattern-and-action\"][\"pattern-action-union\"].pattern[\"condition-selection-pattern\"][\"condition-node\"]";

    //logicScript路經
    public static String LOGIC_SCRIPT_PATH = "[\"logic-expression\"].text[\"logic-script\"].value";

    //textBody路經 .annotation["annotation-text"]["inner-text"]["single-language-text"]["text-body"]
    public static String TEXT_BODY_PATH = "[\"logic-expression\"].annotation[\"annotation-text\"][\"inner-text\"][\"single-language-text\"][\"text-body\"]";

    //levelPath層級，其實是可以用來獲取樹狀圖究竟有多少個節點。
    public static String LEVEL_PATH = "[\"logic-expression\"].text[\"logic-script\"][\"@attributes\"][\"translated-crl-function-id\"]";

    //用於是否有子節點
    //如果值為pattern則為父節點，如果值為action，則為該節點為葉子節點
    public static String IS_LEFT_NODE_PATH = "[\"sub-pattern-and-action\"][\"pattern-action-union\"][\"@attributes\"][\"chosen-element\"]";

    // action動作的基礎路經
    public static final String ACTION_BASIC_PATH = "[\"sub-pattern-and-action\"][\"pattern-action-union\"].";

    // 动作类别，如 charge-discount-action 等动作
    public static final String ACTION_TYPE_PATH = "[\"sub-pattern-and-action\"][\"pattern-action-union\"].action[\"@attributes\"][\"chosen-element\"]";

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
