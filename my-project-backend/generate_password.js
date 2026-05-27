const bcrypt = require('bcrypt');

const password = 'shl13539755908';
const saltRounds = 10;

bcrypt.hash(password, saltRounds, (err, hash) => {
    if (err) {
        console.error(err);
        process.exit(1);
    }
    console.log(hash);
});
