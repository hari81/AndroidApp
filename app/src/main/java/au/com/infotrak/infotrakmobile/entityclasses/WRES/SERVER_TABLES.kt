package au.com.infotrak.infotrakmobile.entityclasses.WRES

class SERVER_TABLES {

    class SERVER_MAKE_TABLE {
        var make_auto: Int? = null
        var makeid: String? = null
        var makedesc: String? = null
        var dbs_id: String? = null
        var created_date: String? = null
        var created_user: String? = null
        var modified_date: String? = null
        var modified_user: String? = null
        var cs_make_auto: Int? = null
        var cat: Boolean? = null
        var Oil: Boolean? = null
        var Components: Boolean? = null
        var Undercarriage: Boolean? = null
        var Tyre: Boolean? = null
        var Rim: Boolean? = null
        var Hydraulic: Boolean? = null
        var Body: Boolean? = null
        var OEM: Boolean? = null

        constructor() {
        }

        constructor(make_auto: Int?, makedesc: String?) {
            this.make_auto = make_auto
            this.makedesc = makedesc
        }
    }

    class SERVER_MODEL_TABLE {
        var model_auto: Int? = null
        var modelid: String? = null
        var modeldesc: String? = null
        var tt_prog_auto: Int? = null
        var gb_prog_auto: Int? = null
        var axle_no: Int? = null
        var created_date: String? = null
        var created_user: String? = null
        var modified_date: String? = null
        var modified_user: String? = null
        var track_sag_maximum: Int? = null
        var track_sag_minimum: Int? = null
        var isPSC: Boolean? = null
        var model_size_auto: Int? = null
        var cs_model_auto: Int? = null
        var cat: Boolean? = null
        var model_pricing_level_auto: Int? = null
        var equip_reg_industry_auto: Int? = null
        var ModelNote: String? = null
        var LinksInChain: Int? = null
        var UCSystemCost: Double? = null
        var ModelImage: ByteArray? = null

        constructor() {
        }

        constructor(model_auto: Int?, modeldesc: String?) {
            this.model_auto = model_auto
            this.modeldesc = modeldesc
        }
    }

    class SERVER_LU_MMTA_TABLE {
        var	mmtaid_auto:	        Int?	 = null
        var	make_auto:	            Int?	 = null
        var	model_auto:	            Int?	 = null
        var	type_auto:	            Int?	 = null
        var	arrangement_auto:	    Int?	 = null
        var	app_auto:	            Int?	 = null
        var	service_cycle_type_auto:Int?	 = null
        var	expiry_date	:	        String?	 = null
        var	created_date	:	    String?	 = null
        var	created_user	:	    String?	 = null
        var	modified_date	:	    String?	 = null
        var	modified_user	:	    String?	 = null
        var	cs_mmtaid_auto	:	    Int?	 = null
    }

    class SERVER_LU_COMPART_TYPE_TABLE {
        var	comparttype_auto	:	Int?	 = null
        var	comparttypeid	:	String?	 = null
        var	comparttype	:	String?	 = null
        var	sorder	:	Int?	 = null
        var	_protected	:	Boolean?	 = null
        var	modified_user_auto	:	Int?	 = null
        var	modified_date	:	String?	 = null
        var	implement_auto	:	Int?	 = null
        var	multiple	:	Boolean?	 = null
        var	max_no	:	Int?	 = null
        var	progid	:	Int?	 = null
        var	fixedamount	:	Int?	 = null
        var	min_no	:	Int?	 = null
        var	getmesurement	:	Boolean?	 = null
        var	system_auto	:	Int?	 = null
        var	cs_comparttype_auto	:	Int?	 = null
        var	standard_compart_type_auto	:	Int?	 = null
        var	comparttype_shortkey	:	String?	 = null
    }

    class SERVER_LU_COMPART_TABLE {
        var	compartid_auto	:	Int?	 = null
        var	compartid	:	String?	 = null
        var	compart	:	String?	 = null
        var	smcs_code	:	Int?	 = null
        var	modifier_code	:	String?	 = null
        var	hrs	:	Int?	 = null
        var	progid	:	Int?	 = null
        var	Left	:	Boolean?	 = null
        var	parentid_auto	:	Int?	 = null
        var	parentid	:	String?	 = null
        var	childoptid	:	Int?	 = null
        var	multiple	:	Boolean?	 = null
        var	fixedamount	:	Int?	 = null
        var	implement_auto	:	Int?	 = null
        var	core	:	Boolean?	 = null
        var	group_id	:	String?	 = null
        var	expected_life	:	Double?	 = null
        var	expected_cost	:	Double?	 = null
        var	comparttype_auto	:	Int?	 = null
        var	companyname	:	String?	 = null
        var	sumpcapacity	:	Int?	 = null
        var	max_rebuilt	:	Int?	 = null
        var	oilsample_interval	:	Int?	 = null
        var	oilchg_interval	:	Int?	 = null
        var	insp_item	:	Boolean?	 = null
        var	insp_interval	:	Int?	 = null
        var	insp_uom	:	Int?	 = null
        var	created_date	:	String?	 = null
        var	created_user	:	String?	 = null
        var	modified_date	:	String?	 = null
        var	modified_user	:	String?	 = null
        var	bowldisplayorder	:	Int?	 = null
        var	track_comp_row	:	Int?	 = null
        var	track_comp_cts_maintype	:	String?	 = null
        var	track_comp_cts_subtype	:	String?	 = null
        var	compart_note	:	String?	 = null
        var	sorder	:	Int?	 = null
        var	hydraulic_inspect_symptoms	:	String?	 = null
        var	cs_compart_auto	:	Int?	 = null
        var	positionid_auto	:	Int?	 = null
        var	allow_duplicate	:	Boolean?	 = null
        var	AcceptEvalAsReading	:	Boolean?	 = null
        var	standard_compartid_auto	:	Int?	 = null
        var	ranking_auto	:	Int?	 = null
    }

    class SERVER_TRACK_COMPART_EXT_TABLE {
        var	track_compart_ext_auto	:	Int?	 = null
        var	compartid_auto	:	Int?	 = null
        var	CompartMeasurePointId	:	Int?	 = null
        var	make_auto	:	Int?	 = null
        var	tools_auto	:	Int?	 = null
        var	budget_life	:	Int?	 = null
        var	track_compart_worn_calc_method_auto	:	Int?	 = null
    }

    class SERVER_TRACK_COMPART_WORN_CALC_METHOD_TABLE {
        var	track_compart_worn_calc_method_auto	:	Int?	 = null
        var	track_compart_worn_calc_method_name	:	String?	 = null
    }

    class SERVER_SHOE_SIZE_TABLE {
        var	Id	:	Int?	 = null
        var	Title	:	String?	 = null
        var	Size	:	Double?	 = null
    }

    class SERVER_TRACK_COMPART_MODEL_MAPPING {
        var	compart_model_mapping_auto	:	Int?	 = null
        var	compartid_auto	:	Int?	 = null
        var	model_auto	:	Int?	 = null
    }

    class SERVER_TYPE {
        var	type_auto	:	Int?		= null
        var	typeid	:	String?		= null
        var	typedesc	:	String?		= null
        var	created_date	:	String?		= null
        var	created_user	:	String?		= null
        var	modified_date	:	String?		= null
        var	modified_user	:	String?		= null
        var	cs_type_auto	:	Int?		= null
        var	blob_auto	:	Int?		= null
        var	blob_large_auto	:	Int?		= null
        var	default_smu	:	Int?		= null
    }

    class SERVER_TRACK_TOOL {
        var	tool_auto	:	Int?	 = null
        var	tool_name	:	String?	 = null
        var	tool_code	:	String?	 = null
    }
}