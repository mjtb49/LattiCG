package com.seedfinding.latticg.math.lattice;

import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.component.BigVector;
import com.seedfinding.latticg.math.lattice.enumeration.Enumerate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnumerateTest {
    public static void main(String[] args) {
        testBKZ17Reverse();
    }

    private static void testBKZ17Reverse() {
        BigMatrix basis = BigMatrix.fromString(
            "{\n" +
                "    { -44389541525945,    -2563735374277,    1919643326751,   34120343161907,  -34524058483785,    2857120431851,  -44296256836593,  -25331898860445,  -22332526090201,    2548979409563,   -3146024681985,    4516321902483,   13735989355415,    8763154157899,    -232356675857,       3704800707,   -2887070554617 },\n" +
                "    {  25414690081607,    33529990846267,  -33911545502177,    8148045747507,    2099922802871,   26708598976491,   19476546341135,    9550626723171,  -20153828340441,  -17897080937573,  -24835491796225,   -8980834047853,   17360624212119,  -11545135947189,    -580924425233,     609342242499,   -5778465854713 },\n" +
                "    {  36302748918381,    -2017043114391,   -9778401484619,   21645590383377,  -10345062548419,    6853987857401,  -37539297650939,  -22545933939935,   37141821989645,   24421989849737,    8708253803093,   -6887944242127,  -30988356081955,   -2940955387367,     495279641253,   -1687162606015,   -4359875881043 },\n" +
                "    { -28139147364290,   -22312104335770,    4148925644142,   28032038075350,   24285720903198,    2709950342854,  -17676512205234,   57882328965942,    3441812406782,   -8857806533338,  -17600189678290,   -3441099054954,   11223101793246,  -33666002028154,     159395226126,    -434675051530,     920593400766 },\n" +
                "    { -18845060522773,     3408183292431,   11782818990691,   34159859654183,   -7704965439333,   26041474393087,  -26289646539373,   43253294381463,   -2932139544757,  -15878765771537,    8402913809347,   -1338645292025,  -24432741385989,    2381594985695,    -392580567821,    2900071691639,   -1743929102933 },\n" +
                "    {   6674714629448,   -15155666337880,  -13603230971256,   36279758269416,   -3449166126648,   -1237912677848,  -35443956962552,   24352287392360,  -24726161263032,   -4354706928472,  -20699385965688,   17621424357608,  -24270511729976,    4920542015272,    1275111441416,   -2375292590232,   -1487309736120 },\n" +
                "    {  14408164060809,   -39762644769195,    -582794055631,    4762877283037,  -11989378929127,    8755923310245,    5622919176769,   39854957951917,   17232603780777,  -18859371037707,   -1886311291567,   16142584605053,    7175771535417,    5802235995717,     437842954593,   -5834511915955,    1591285179081 },\n" +
                "    { -24693740277652,    29911045924348,  -28034626235316,   25675105414236,    6910858824492,   -9098507368004,   16833048825100,   -7053830875620,  -11366655127060,   -8887988868740,   18928589967820,   30433938529244,    4158731502764,    -982673767108,     297395380876,  -11023880503908,   -2596984134804 },\n" +
                "    {  35003335260660,    12431039587556,  -13842293699308,    7608380475780,    6388388619572,    1873763487524,  -23359537575340,   22494309604804,   16122640794740,  -30538976125596,   -6185980961900,   -2628081224188,  -19463058597964,   -2047338934364,   -1190208274220,   -2420909157820,    4263521525492 },\n" +
                "    {   3134268671932,   -22451453644020,   23076620010524,   10165398792172,    2704809117564,   32944393639884,  -24623401686564,  -19276142806356,  -10970982038724,   20110909343884,   26931776383900,  -12397446137492,   34182285409020,    2287826109772,     149764213084,     519829922860,   -7937345327428 },\n" +
                "    {  -7916911246630,   -20443899538734,    6258080935786,  -40696725165022,    6923012036218,  -31764327535118,    7367258291210,   16472773659714,    9168236905498,  -19143917692142,   24759376261802,  -23660331020702,   -8664416064582,   34414635219506,    -275321195702,   -1339479616894,   -6080785568422 },\n" +
                "    {   8338043711476,    24669832061668,    3096023741204,    1339667691396,   -3829667939532,   -3689604420316,  -30743088803756,   -8220267500604,     641367063156,   38510021548900,  -39669682601580,   -2933432212476,   12618158927284,    8797007080868,     117612414676,    2300364480580,   -4392113793804 },\n" +
                "    { -10845234448258,    -5723629272666,   32394808334254,   16001461086486,   -9669754387874,  -15598821771770,   -2862001281906,   27618277926006,  -19980676855234,   -7047430500250,   34823666737006,    2095453779414,    3288845529118,     302594879686,     184489404494,   -7405748552394,    -994436033538 },\n" +
                "    { -21297325867282,    -2038058295466,    2573342989214,  -12292377643450,   -4241392975922,  -10414816411978,  -36252544452738,    4225715524774,   13671817326254,   14028121486358,   11681852453214,    -225400538874,    8676061883278,  -41463631437962,    -267939780290,    9870339310438,    2852387460718 },\n" +
                "    { -24653990591439,     7182135316189,   -2306235850215,  -30689982383451,  -14458453723583,   17104059023277,   19787353623209,   -1040264503307,    6504309979473,   20301086059901,    6414904880185,    1839312751173,   -1269147722399,  -24231014160307,     107795470025,    2810893928853,    9948341957233 },\n" +
                "    {   7815283902776,     9476207459544,    3732268677112,   42079475983512,  -10649651583816,  -29614288489896,   -8805547442312,    7912800556568,   -7603088558024,    8331294179288,   -2816769360136,   -2202980130920,   -7239409613896,    3519753856344,    -416769345928,    -477384261352,    3084051629880 },\n" +
                "    { -16691284157604,    17253202726444,   -9574078574404,  -13489416402932,   -1600493669092,   -6966316874516,  -20532432904068,   13281292287180,   18995859516124,   -3796888442964,    5382698527804,  -16996956832372,   20053693283484,   21292184729196,    -300368366596,    3444956187212,    3779626625628 }\n" +
                "}\n"
        ).transpose();

        BigVector lower = BigVector.fromString("211106232532992,  211106232532981,  210828868589894,  199388147328707,  161385748837116,  108479823158593,  185398950615714,  185126754296559,   73966775769528,   62839209804621,   83194595169726,  145472338376155,  -22881604128716,  -51152864657895,  119381387175578,  107356151384967,  249574878786160");
        BigVector upper = BigVector.fromString("281474976710656,  281474976710645,  281197612767558,  269756891506371,  231754493014780,  178848567336257,  255767694793378,  255495498474223,  144335519947192,  133207953982285,  153563339347390,  215841082553819,   47487140048948,   19215879519769,  121580410431130,  124948337429383,  267167064830576");

        List<BigVector> correct = new ArrayList<>();

        correct.add(BigVector.fromString("254379274209980, 229881165412876, 262819585900316, 248202398435052, 169769454238332, 156270167756492, 200884390073564, 205085377595820, 90941704578620, 123842014818188, 152695241770652, 211733147023468, 40984347004412, 14489354574924, 119855897990236, 110676056183596, 263271954198972"));
        correct.add(BigVector.fromString("242888083933198, 263522233385462, 273582247532990, 260473425816038, 175710505548526, 173087775976278, 187454141855646, 188623197037126, 115876125621198, 94940116978358, 88051907597182, 195065203261606, -1448313394514, -3127107600362, 119421831581022, 108959214839046, 261430817367950"));
        correct.add(BigVector.fromString("221266933080978, 218905545117994, 233862345951970, 208804219203130, 182189126044338, 132153684867530, 222993717991170, 185697263851482, 80004321936338, 75480253254762, 116411008304418, 186720220740474, 22931085962994, -25931960882934, 121037934048578, 112944597170458, 252809165040658"));
        correct.add(BigVector.fromString("242565830324907, 225543473837263, 244522705090083, 237967492071143, 166174218474075, 154461612528319, 185992584910163, 195787966703191, 84057202113803, 93684566712239, 140754939246467, 171331025999047, 29141755437755, -46402734011489, 119688516111539, 113377027602999, 262167734825835"));
        correct.add(BigVector.fromString("266769535492704, 230851463488224, 268793093167456, 242860242046432, 162438293891168, 135379324596448, 250890550398816, 186114496007136, 112195202207328, 103146873921248, 148249994061152, 204848231672288, 35818811348064, -43845983752992, 120554071317344, 123158869682144, 253915669183072"));
        correct.add(BigVector.fromString("241900501677363, 272725111961783, 278712204054507, 216716708391183, 228922923840867, 159217329827111, 190755488662427, 225644369070847, 138773313194131, 67882164107415, 90384707273291, 214835170909167, 27796601872067, -24991715374329, 121204082523131, 113438596500447, 253645288171507"));
        correct.add(BigVector.fromString("265953429562198, 234964740231070, 258625913346630, 261880654123982, 216913006903990, 130512154323838, 223869580834982, 185241556452014, 140989739497494, 93525852251486, 104680107318534, 189747247595406, -8922448616586, -32655388205762, 119749790115686, 123063864388206, 261429013357782"));
        correct.add(BigVector.fromString("235583006040879, 270908131898627, 275930819331143, 205255285775419, 202245681743647, 161390575182387, 234577503363511, 204742078579947, 141081870432783, 119133225251427, 102766611768871, 176180005750939, 24332449131519, 11449261905299, 120544028164503, 114821375383371, 263833993022703"));
        correct.add(BigVector.fromString("243398289943655, 280384339358171, 279663088008255, 247334761758931, 191596030159831, 131776286692491, 225771955921199, 212654879136515, 133478781874759, 127464519430715, 99949842408735, 173977025620019, 17093039517623, 14969015761643, 120127258818575, 114343991122019, 266918044652583"));
        correct.add(BigVector.fromString("266541081025707, 252090273928911, 277245780570147, 211261565169895, 164983635254363, 119003568592063, 239277684830035, 225702853261399, 85520528221963, 73224882946479, 114917835207043, 175817456649927, -12062602285893, 13505827114399, 121513010374323, 123768392174647, 258220155231595"));
        correct.add(BigVector.fromString("225536400801612, 253124816714588, 243048652762668, 249916666835132, 228447850260492, 129790030615836, 191226554197228, 235516355362940, 89220673821900, 85100289739484, 106451451479980, 204910576880700, 19357949189516, -6555449254756, 119736659924588, 113222553457660, 257506214160972"));
        correct.add(BigVector.fromString("218493365701042, 222376582040778, 261761625599490, 212786865562330, 162114279862994, 164499390828394, 207027858787362, 236232961291898, 119672314498546, 83606844922890, 103957314186306, 180969303483418, 42562786769682, -13871664835926, 119925697408610, 114685814016954, 249751717059122"));
        correct.add(BigVector.fromString("242267434645429, 213142703379985, 278351121722173, 207040995440377, 208679031381509, 162958913881633, 214713291899405, 228248629448073, 88095779412309, 118086253111089, 88555067864541, 193312907935001, -6123623319131, -51095974377151, 119847428380333, 123612371703209, 266271913627381"));
        correct.add(BigVector.fromString("213267107006463, 228037357683091, 213250356608407, 226962617770827, 184153914992879, 173793981032387, 199566072733703, 201332280306939, 97052859246815, 66648917983475, 89990255376759, 183286121669035, -4790500952113, -23852960776925, 120460864076263, 123250417414491, 252059671971263"));
        correct.add(BigVector.fromString("221082390909239, 237513565142635, 216982625285519, 269042093754339, 173504263409063, 144179692542491, 190760525291391, 209245080863507, 89449770688791, 74980212162763, 87173486016623, 181083141538115, -12029910566009, -20333206920581, 120044094730335, 122773033153139, 255143723601143"));
        correct.add(BigVector.fromString("219511512898042, 270845278268274, 259814358674314, 235432499070402, 214615562485658, 155086343614610, 224253716027946, 197459675520994, 98481738325818, 95127867811762, 123623219696330, 180592277715970, -19731993061158, 1770560843986, 121263684236650, 115980230228514, 265147511901306"));
        correct.add(BigVector.fromString("243974523100421, 255093393414433, 232583154276621, 267762475784329, 219955701434453, 176237832972849, 216699504426205, 191268755159065, 83459772733605, 126862888336449, 90475844193709, 190279410122921, 33066769849845, 16182153109329, 119660900637565, 113611302289977, 260743310993477"));
        correct.add(BigVector.fromString("229734767714019, 216017305000103, 280733090859291, 202035357639295, 203078628346387, 124939835431959, 190780693143499, 236511562869615, 120199903885379, 113459610560135, 113125755990395, 186912655381343, -19980110063245, -11971377027081, 121576246996523, 117531986299471, 255338658553251"));
        correct.add(BigVector.fromString("231451570144113, 215358232853789, 250988656776025, 228262789378789, 224237814163841, 141025005900269, 208346651168745, 198685952056373, 87303967449233, 125543980573629, 121534374380921, 183390635395717, 13024722385057, -12159027838323, 119418008028169, 118697205796309, 253735286260145"));
        correct.add(BigVector.fromString("235278772961929, 249467224566357, 223430598583345, 261701775797981, 195296837796377, 178168362818213, 211574718355009, 189264174587821, 141469922880169, 96718051262453, 97159928207697, 165658527812989, 47478390869049, -39118279366075, 120660390083937, 108112148596813, 257865217029833"));
        correct.add(BigVector.fromString("235643665450170, 216670828109618, 276931942691914, 264944334585730, 184081480570458, 119745832263762, 220075253886698, 221758381094306, 121199932416506, 88685590352754, 85894304674698, 150506257673666, 33158425806746, -13842483734382, 121480132698666, 110820465854434, 253193246975802"));
        correct.add(BigVector.fromString("232403250023341, 264640108553897, 244308315671541, 204080858412369, 220997598017917, 146582115518521, 235377409111621, 214427499141473, 82172104701005, 75911879111369, 129846258087317, 164722668940913, -22413416313827, -38115756050855, 120439867202021, 121675475977345, 252361021313261"));
        correct.add(BigVector.fromString("238283881077300, 237122468388900, 232637544335188, 269392846100164, 190990281689460, 109834520133220, 249610772261012, 219995470675716, 88056567426228, 111301503058084, 93688722300372, 176991920728900, -13542250937356, -23529268287772, 121049890334484, 124712853248900, 256851324260148"));
        correct.add(BigVector.fromString("211270122280138, 263845666444802, 254822029159130, 231165082491602, 186540017276778, 113194065556514, 204278796713594, 244761972831730, 95316205708298, 130635911484482, 97042174323738, 161359965594386, 29657856416426, 3342764545634, 121468393132986, 123529809625650, 252160349428554"));
        correct.add(BigVector.fromString("256373536128131, 253575450670535, 227418213232571, 255279604225183, 188265925009843, 165205235821367, 237135298170987, 231228949733263, 105036578351587, 103349512406951, 114550268677147, 147034149824895, 11285459551507, 15236020894487, 120084813157067, 119197284819567, 264053081580355"));
        correct.add(BigVector.fromString("268606201107116, 217625846872892, 238139601969292, 245087149384604, 210733131010412, 174266965750524, 210433380441420, 215300836917596, 100811314740268, 92542142097084, 117251287082508, 164169373498140, 8416385487596, -26808534968708, 120426215619276, 123289158275292, 250093448828332"));
        correct.add(BigVector.fromString("262964741698402, 259023503305914, 250553814034226, 252262879370314, 194528899565442, 122034145918554, 201451692042322, 253974990149354, 92442986986914, 103893823801850, 85473494834034, 175728841202570, -9858895465022, -19996759117926, 120578553225362, 117663944280618, 252644244328418"));
        correct.add(BigVector.fromString("256882101695949, 259166382757449, 216110543115029, 201891184803313, 208189678822813, 154094575096281, 239390283632485, 207837681339393, 112164470637165, 88278303094377, 100180355252405, 188867273026321, 37913205955645, -47817565531143, 121245243553541, 117725328173857, 265556647789325"));
        correct.add(BigVector.fromString("270237217663585, 216277241704781, 217418949340105, 238177681619605, 210017619136369, 115334490413341, 238128135438169, 201461902880485, 114494858179969, 74445292056045, 151083985042409, 149241514039349, 33045251615889, -13175198449731, 119984046059897, 121056294028933, 263093169980577"));
        correct.add(BigVector.fromString("278575261375061, 240947073766449, 220514973081309, 239517349311001, 206187951196837, 111644885993025, 207385046634413, 193241635379881, 115136225243125, 112955313604945, 111414302440829, 146308081826873, 45663410543173, -4378191368863, 120101658474573, 123356658509513, 258701056186773"));
        correct.add(BigVector.fromString("238310751106963, 266205638622103, 248247578184011, 221572896986863, 180070445841859, 139138204941831, 221239045665531, 252934725646047, 98947296966387, 124194909861751, 91888399714219, 177568153953743, -11128043187421, -44227773278233, 120686348695387, 120474838209471, 262592586285651"));
        correct.add(BigVector.fromString("259767320492968, 225152377083528, 260177624139752, 238318244416968, 183660236754472, 120116440119048, 244842831879784, 206538356515400, 82663998851240, 130005857607560, 147270175530216, 167239805189832, 27629415066408, -49598795378680, 120694311715688, 115316847698760, 263875335484840"));
        correct.add(BigVector.fromString("246036867247691, 253442753484783, 248505351449283, 217892645889799, 198519924434939, 138495753324511, 236528282350579, 235455959265399, 92183793781931, 81230872594127, 135004021989411, 190962726805735, 41202051484763, -47514970651457, 121429732958035, 110319579621463, 264562211755787"));
        correct.add(BigVector.fromString("243336227672698, 272827838740978, 257184909465610, 250334540773442, 189284318560282, 120706912102162, 236338880761514, 229633130019426, 139463668754362, 84255784322610, 145653875265354, 178300927524482, -3398613341862, -29004346083502, 121189969181162, 112997966603426, 253671198352634"));
        correct.add(BigVector.fromString("227494886605647, 268421168095907, 220885113237863, 242794690925275, 195629228624191, 117204296960467, 214872404934871, 223372048254347, 98155485003311, 131652637324291, 124867706230599, 167645881414459, 8020302516767, -5290826739405, 120256928343223, 115559672302571, 250055958918415"));

        long start = System.nanoTime();

        List<BigVector> results = Enumerate.enumerate(basis, lower, upper, new BigVector(17))
            .collect(Collectors.toCollection(ArrayList::new));

        long end = System.nanoTime();

        System.out.printf("elapsed: %.2f%n", (end - start) / 1.0e9);

        assertEquals(correct.size(), results.size());
        assertTrue(results.containsAll(correct));
    }
}
