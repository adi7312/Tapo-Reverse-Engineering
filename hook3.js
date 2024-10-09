const classLoaders = Java.enumerateClassLoadersSync();
for (const classLoader in classLoaders) {
    try {
        classLoader.findClass("com.tplink.cloud.api.AccountV2Api");
        Java.classFactory.loader = classLoader;
        console.log(`classLoader=${classLoader}`)
        break;
    } catch {
        continue;
    }
}

Java.perform(() => {
  let SignatureInterceptor = Java.use("m7.m");
  SignatureInterceptor["$init"].implementation = function (str, str2) {
    console.log(`SignatureInterceptor constructor is called: accessKey=${str}, secret=${str2}`);
    this["$init"](str, str2);
};
   SignatureInterceptor["a"].implementation = function (requestBody) {
    let result = this["a"](requestBody);
    console.log(`SignatureInterceptor.createSignatureAndEncode presignature=${result}`);
    return result;
  };
  SignatureInterceptor["b"].implementation = function (encodedReqSignature, timestamp, nonce, apiUrl) {
    console.log(`SignatureInterceptor.sign is called: encodedReqSignature=${encodedReqSignature}, timestamp=${timestamp}, nonce=${nonce}, apiUrl=${apiUrl}`);
    let result = this["b"](encodedReqSignature, timestamp, nonce, apiUrl);
    console.log(`SignatureInterceptor.sign result=${result}`);
    return result;
};
})
