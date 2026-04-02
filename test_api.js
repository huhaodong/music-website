const axios = require('axios');

async function test() {
    try {
        const loginRes = await axios.post('http://localhost:8888/api/auth/login', {
            username: 'admin',
            password: '123'
        });
        const token = loginRes.data.data.token;
        console.log("Token:", token);
        
        const treeRes = await axios.get('http://localhost:8888/system/organization/tree', {
            headers: {
                Authorization: "Bearer " + token
            }
        });
        console.log("Tree Response:", treeRes.data);
    } catch (e) {
        console.error("Error:", e.response ? e.response.data : e.message);
    }
}
test();
