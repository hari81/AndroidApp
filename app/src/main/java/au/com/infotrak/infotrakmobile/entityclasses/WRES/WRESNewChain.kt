package au.com.infotrak.infotrakmobile.entityclasses.WRES

class WRESNewChain {

    class ComponentType {
        var compartid_auto: Long = 0
        var compartid: String = ""
        var compart: String = ""
        var defaultTool: String = ""
        var method: String = ""
        constructor(compartid_auto: Long, compartid: String, compart: String, defaultTool: String, method: String) {
            this.compartid_auto = compartid_auto
            this.compartid = compartid
            this.compart = compart
            this.defaultTool = defaultTool
            this.method = method
        }


        /**
         * Pay attention here, you have to override the toString method as the
         * ArrayAdapter will reads the toString of the given object for the name
         *
         * @return contact_name
         */
        override fun toString(): String {
            return compartid
        }
    }

    class Brand {
        var make_auto: Long = 0
        var makedesc: String = ""
        constructor(make_auto: Long, makedesc: String) {
            this.make_auto = make_auto
            this.makedesc = makedesc
        }

        constructor()

        /**
         * Pay attention here, you have to override the toString method as the
         * ArrayAdapter will reads the toString of the given object for the name
         *
         * @return contact_name
         */
        override fun toString(): String {
            return makedesc
        }
    }

    class ShoeSize {
        var id: Long = 0
        var title: String = ""

        constructor()

        constructor(id: Long, title: String) {
            this.id = id
            this.title = title
        }

        /**
         * Pay attention here, you have to override the toString method as the
         * ArrayAdapter will reads the toString of the given object for the name
         *
         * @return contact_name
         */
        override fun toString(): String {
            return title
        }
    }

}