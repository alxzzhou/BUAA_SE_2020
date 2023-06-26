import axios from "axios";
import qs from "qs";

export default {
    data: () => ({
        ws_address: 'ws://192.168.143.126:9090',
        firstNav: true
    }),
    async sendData(obj, dest) {
        await axios.post(dest, qs.stringify(obj))
            .then(res => {
                console.log(res)
            })
    },
}