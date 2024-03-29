package com.chinamobile.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FreeUnitTypeConstants {
    public static final Map<String, String> DATA_MAP;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("1011", "FreeDurationForOffnetCall");
        map.put("1010", "FreeDurationForOnnetCall");
        map.put("1003", "FreeItemsForOnNetSMS");
        map.put("1025", "FreeTimesForLocalCall");
        map.put("523420240", "GPRSBonusFlux");
        map.put("1651951397", "LocalDurationData");
        map.put("551951301", "LocalFluxData");
        map.put("151952684", "RoamDurationData");
        map.put("151952615", "RoamFluxData");
        map.put("1996755776", "VirtualCurrency");
        map.put("10000002", "CN_DATA_DEMO_WITH_FLAG");
        map.put("10000001", "eee");
        map.put("10000092", "CN_NON_NRTRDE_FU");
        map.put("10000091", "CN_NRTRDE_FU");
        map.put("10000042", "TestFreeDurationForOnnetCall");
        map.put("3000012", "Greater China GPRS");
        map.put("2000001", "Local Intra SMS");
        map.put("3000054", "Greater China GPRS Bucket R");
        map.put("3000052", "China Roam GPRS Bucket R");
        map.put("3000018", "BeltAndRoad GPRS");
        map.put("1000004", "Bay Area Voice");
        map.put("3000004", "Bay Area GPRS");
        map.put("3000050", "Bay Area GPRS Unlimted");
        map.put("3000051", "Bay Area GPRS Bucket E");
        map.put("3000015", "Asia GPRS");
        map.put("3000055", "Asia GPRS Bucket R");
        map.put("3000013", "America GPRS");
        map.put("3000003", "China Roam GPRS");
        map.put("10000050", "Greater China GPRS Example Test");
        map.put("2000003", "IDD SMS");
        map.put("510000029", "AerialRoaming");
        map.put("1000001", "Local Voice");
        map.put("1000008", "Local Voice Intra");
        map.put("1000007", "Local Voice Inter");
        map.put("3000006", "Local Video Con");
        map.put("3000002", "Local Unlimted GPRS");
        map.put("3000005", "Local UTV");
        map.put("3000007", "Local Social and Ent");
        map.put("2000004", "Local SMS");
        map.put("3000030", "Local My Vedio Pass");
        map.put("3000031", "Local JOOX music");
        map.put("2000002", "Local Inter SMS");
        map.put("3000048", "Local GPRS Bucket F");
        map.put("3000047", "Local GPRS Bucket C");
        map.put("3000001", "Local GPRS Bucket A");
        map.put("3000045", "Letv GPRS");
        map.put("1000005", "IDD TO China Voice");
        map.put("1000003", "Greater China Voice");
        map.put("3000036", "Greater China Go Pass GPRS");
        map.put("1000002", "Global IDD Voice");
        map.put("3000008", "Global GPRS");
        map.put("3000053", "Global GPRS Bucket R");
        map.put("3000014", "Europe GPRS");
        map.put("3000046", "Data Roaming Zone B-D GPRS");
        map.put("1000006", "China Voice");
        map.put("3000049", "China Roam GPRS Unlimted");
        map.put("3000035", "Thereafter");
        map.put("3000011", "Southeast Asia GPRS");
        map.put("1000016", "Global Roaming Voice NRTRDE");
        map.put("1000015", "China Voice NRTRDE");
        map.put("1000014", "Bay Area Voice NRTRDE");
        map.put("3000016", "Oceania GPRS");
        map.put("510000034", "AntiguaAndBarbuda");
        map.put("510000033", "Anguilla");
        map.put("510000032", "Algeria");
        map.put("510000031", "Albania");
        map.put("510000030", "Afghanistan");
        map.put("3000019", "Middle East GPRS");
        map.put("3000033", "Aero GPRS");
        map.put("10000051", "Local Intra SMS Test");
        map.put("10000052", "Greater China Voice Example Test");
        map.put("10000053", "Local Intra SMS Test_10000053");
        map.put("10000054", "Greater China GPRS Bucket R Example Test");
        map.put("3000059", "Bay Area GPRS Unlimted Bucket E");
        map.put("3000056", "Greater China GPRS Unlimted Bucket A");
        map.put("3000057", "Greater China GPRS Unlimted Bucket R");
        map.put("2000005", "ISMS TO China");
        map.put("2000006", "Local Intra SMS1");
        map.put("2000007", "Local Inter SMS1");
        map.put("3000058", "Local Unlimted GPRS Bucket C");
        map.put("510000035", "Argentina");
        map.put("510000036", "Armenia");
        map.put("510000037", "Aruba");
        map.put("3000065", "Asia GPRS MonthPass Bucket R");
        map.put("510000001", "Australia");
        map.put("510000038", "Austria");
        map.put("510000039", "Azerbaijan");
        map.put("510000040", "Bahamas");
        map.put("510000041", "Bahrain");
        map.put("510000002", "Bangladesh");
        map.put("510000042", "Barbados");
        map.put("3000060", "Bay Area GPRS DayPass Bucket R");
        map.put("510000044", "Belarus");
        map.put("510000045", "Belgium");
        map.put("510000162", "Belize");
        map.put("510000046", "Benin");
        map.put("510000047", "Bermuda");
        map.put("510000163", "Bhutan");
        map.put("510000164", "Bolivia");
        map.put("510000048", "BosniaandHerzegovina");
        map.put("510000049", "Brazil");
        map.put("510000165", "BritishVirginIslands");
        map.put("510000003", "Brunei");
        map.put("510000050", "Bulgaria");
        map.put("510000004", "Cambodia");
        map.put("510000051", "Cameroon");
        map.put("510000052", "CanadaAndUSA");
        map.put("510000166", "CapeVerde");
        map.put("510000053", "CaymanIslands");
        map.put("510000054", "CentralAfricanRep");
        map.put("510000055", "Chile");
        map.put("510000056", "Colombia");
        map.put("510000057", "CongoDR");
        map.put("510000058", "CostaRica");
        map.put("510000059", "CotedIvoire");
        map.put("510000060", "Croatia");
        map.put("510000061", "Cyprus");
        map.put("510000062", "CzechRepublic");
        map.put("510000063", "Denmark");
        map.put("510000064", "Dominica");
        map.put("510000065", "Ecuador");
        map.put("510000066", "Egypt");
        map.put("510000067", "ElSalvador");
        map.put("510000068", "Estonia");
        map.put("3000063", "Europe GPRS DayPass Bucket R");
        map.put("510000069", "FaroeIslands");
        map.put("510000005", "Fiji");
        map.put("510000070", "Finland");
        map.put("510000071", "France");
        map.put("510000072", "FrenchGuiana");
        map.put("510000073", "FrenchPolynesia");
        map.put("510000074", "Gabon");
        map.put("510000167", "Gambia");
        map.put("510000075", "Georgia");
        map.put("510000076", "Germany");
        map.put("510000077", "Ghana");
        map.put("510000168", "Gibraltar");
        map.put("3000066", "Global GPRS MonthPass Bucket R");
        map.put("3000067", "Greater China GPRS DayPass Bucket R");
        map.put("3000064", "Greater China GPRS MonthPass Bucket R");
        map.put("510000078", "Greece");
        map.put("510000169", "Greenland");
        map.put("510000079", "Grenada");
        map.put("510000080", "Guadeloupe");
        map.put("510000081", "Guam");
        map.put("510000082", "Guatemala");
        map.put("510000083", "Guyana");
        map.put("510000084", "Haiti");
        map.put("510000085", "Honduras");
        map.put("510000086", "Hungary");
        map.put("510000087", "Iceland");
        map.put("510000006", "India");
        map.put("510000007", "Indonesia");
        map.put("510000088", "Iran");
        map.put("510000089", "Ireland");
        map.put("510000008", "Israel");
        map.put("510000090", "Italy");
        map.put("510000091", "Jamaica");
        map.put("510000009", "Japan");
        map.put("510000092", "Jordan");
        map.put("510000094", "Kenya");
        map.put("510000095", "Kuwait");
        map.put("510000096", "Kyrgyzstan");
        map.put("510000097", "Laos");
        map.put("510000098", "Latvia");
        map.put("510000099", "Liberia");
        map.put("510000100", "Liechtenstein");
        map.put("510000101", "Lithuania");
        map.put("2000008", "Local SMS1");
        map.put("510000102", "Luxembourg");
        map.put("510000103", "Madagascar");
        map.put("510000104", "Malawi");
        map.put("510000010", "Malaysia");
        map.put("510000105", "Maldives");
        map.put("510000170", "Mali");
        map.put("510000106", "Malta");
        map.put("510000107", "MaritimeRoaming");
        map.put("510000108", "Mauritius");
        map.put("510000109", "Mexico");
        map.put("510000110", "Moldova");
        map.put("510000171", "Monaco");
        map.put("510000011", "Mongolia");
        map.put("510000111", "Montenegro");
        map.put("510000172", "Montserrat");
        map.put("510000112", "Morocco");
        map.put("510000113", "Mozambique");
        map.put("510000012", "Myanmar");
        map.put("510000013", "Nauru");
        map.put("510000014", "Nepal");
        map.put("510000114", "Netherlands");
        map.put("510000015", "NewZealand");
        map.put("510000115", "Nicaragua");
        map.put("510000116", "Niger");
        map.put("3000062", "North America GPRS DayPass Bucket R");
        map.put("510000117", "Norway");
        map.put("510000118", "Oman");
        map.put("510000016", "Pakistan");
        map.put("510000119", "Panama");
        map.put("510000017", "PapuaNewGuinea");
        map.put("510000120", "Paraguay");
        map.put("510000121", "Peru");
        map.put("510000018", "Philippines");
        map.put("510000122", "Poland");
        map.put("510000123", "Portugal");
        map.put("510000124", "PuertoRico");
        map.put("510000019", "Qatar");
        map.put("510000020", "RepublicofKorea");
        map.put("510000125", "Romania");
        map.put("510000126", "Russia");
        map.put("510000127", "Rwanda");
        map.put("2000009", "SMS China and HK1");
        map.put("2000010", "SMS China and HK");
        map.put("510000128", "SanMarino");
        map.put("510000021", "SaudiArabia");
        map.put("510000129", "Serbia");
        map.put("510000130", "Seychelles");
        map.put("510000131", "SierraLeone");
        map.put("3000068", "Singapore GPRS DayPass Bucket R");
        map.put("510000022", "Singapore");
        map.put("510000132", "SlovakRepublic");
        map.put("510000133", "Slovenia");
        map.put("510000134", "SouthAfrica");
        map.put("510000135", "Spain");
        map.put("510000023", "SriLanka");
        map.put("510000136", "St.Kitts/Nevis");
        map.put("510000137", "St.Lucia");
        map.put("510000138", "St.Martin");
        map.put("510000139", "St.VincentAndGrenadines");
        map.put("510000140", "Sudan");
        map.put("510000141", "Suriname");
        map.put("510000142", "Swaziland");
        map.put("510000143", "Sweden");
        map.put("510000144", "Switzerland");
        map.put("510000145", "Tajikistan");
        map.put("510000146", "Tanzania");
        map.put("510000024", "Thailand");
        map.put("510000025", "Tonga");
        map.put("3000061", "Top Asia GPRS DayPass Bucket R");
        map.put("510000147", "Trinidad");
        map.put("510000148", "Tunisia");
        map.put("510000026", "Turkey");
        map.put("510000174", "TurksAndCaicos");
        map.put("510000149", "UAE");
        map.put("510000151", "UK");
        map.put("510000154", "USVirginIslands");
        map.put("510000150", "Uganda");
        map.put("510000152", "Ukraine");
        map.put("510000153", "Uruguay");
        map.put("510000156", "Uzbekistan");
        map.put("510000027", "Vanuatu");
        map.put("510000158", "Venezuela");
        map.put("510000028", "Vietnam");
        map.put("510000159", "Yemen");
        map.put("510000160", "Zambia");
        map.put("510000175", "Zimbabwe");
        map.put("3000073", "Asia GPRS Bucket F");
        map.put("3000070", "Bay Area GPRS Bucket F");
        map.put("3000074", "Global GPRS Bucket F");
        map.put("3000072", "Greater China GPRS Bucket F");
        map.put("3000009", "Local MyTV Super");
        map.put("3000069", "Local Unlimted GPRS Bucket F");
        map.put("3000076", "Asia GPRS Bucket E");
        map.put("3000077", "Global GPRS Bucket E");
        map.put("3000075", "Greater China GPRS Bucket E");
        map.put("3000078", "Greater China Unlimted GPRS Bucket E");
        map.put("5054", "Bay Area 3 places 25GB Data Bonus");
        map.put("5005", "Free Local Data Volume (MyLink)");
        map.put("5006", "Free Local Intra SMS");
        map.put("5055", "Free Local SMS");
        map.put("5009", "Free PRBT Song");
        map.put("5010", "Free unit for adding Buddy List");
        map.put("5030", "Local Data Activation Bonus");
        map.put("5025", "Macau Mobile Data Usage");
        map.put("5029", "Mainland China, HK and Macau Data Usage");
        map.put("4500", "Mobile Data Volume");
        map.put("5048", "Voice of calling Mainland China IDD");
        map.put("5047", "local airtime usage");
        map.put("2000011", "Local MMS");
        map.put("1000009", "IDD Voice");
        map.put("3000040", "MySIM Asia Zone A");
        map.put("3000041", "MySIM Asia Zone B");
        map.put("3000043", "MySIM Europe Zone A");
        map.put("3000044", "MySIM Europe Zone B");
        map.put("3000042", "MySIM N America");
        map.put("3000079", "Bay Area Unlimted GPRS DayPass Bucket R");
        map.put("3000082", "Europe Unlimted GPRS DayPass Bucket R");
        map.put("3000083", "Greater China Unlimted GPRS DayPass Bucket R");
        map.put("3000081", "North America Unlimted GPRS DayPass Bucket R");
        map.put("3000084", "Singapore Unlimted GPRS DayPass Bucket R");
        map.put("3000080", "Top Asia Unlimted GPRS DayPass Bucket R");
        map.put("510000176", "Dominica1");
        map.put("510000177", "Dominica2");
        map.put("510000178", "Dominica3");
        map.put("1000010", "Global Roaming Voice");
        map.put("1000011", "IDD Voice1597");
        map.put("510000181", "Kosovo");
        map.put("510000179", "US.PuertoRico");
        map.put("510000180", "VaticanCityState");
        map.put("1000012", "Video Call Voice");
        map.put("3000085", "Local Slash");
        map.put("3000086", "China Roam GPRS F");
        map.put("510000185", "InternationalNetworks1");
        map.put("510000186", "InternationalNetworks2");
        map.put("510000183", "Macedonia");
        map.put("510000182", "MarianaIs");
        map.put("510000184", "NetherlandsAntilles");
        map.put("510000187", "Palestine");
        map.put("3000094", "Asia GPRS Bucket A");
        map.put("3000091", "Bay Area GPRS Bucket A");
        map.put("510000188", "Botswana");
        map.put("3000090", "China Roam GPRS Bucket A");
        map.put("3000088", "China only GPRS Bucket F");
        map.put("3000093", "Greater China GPRS Bucket A");
        map.put("3000089", "Local GPRS Bucket A_");
        map.put("3000087", "Multi-Country GPRS");
        map.put("3000092", "Global GPRS Bucket A");
        map.put("3000096", "Mainland Prevention");
        map.put("2000012", "Global SMS");
        map.put("3000101", "Macau Prevention");
        map.put("3000098", "Taiwan Prevention");
        map.put("3000099", "Others Country Prevention 1MB");
        map.put("110773", "Local GPRS PO");
        map.put("110175", "Local SMS PO");
        map.put("110497", "Local Unmlimted GPRS 128Kbps");
        map.put("110358", "Local Voice PO");
        map.put("3000100", "Trial GPRS");
        map.put("2000013", "Trial MMS");
        map.put("2000014", "Trial SMS");
        map.put("1000013", "Trial Voice");
        DATA_MAP = Collections.unmodifiableMap(map);
    }


}
