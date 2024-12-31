#!/bin/bash -ex

root=$WORKSPACE
apk_signer_path=$root/cicd
tool_path=$ANDROID_HOME/build-tools/28.0.3
debug_certi_path=$apk_signer_path/debug_certi
release_certi_path=$apk_signer_path/release_certi


## Following parameters needs to be set for the signing the APK
# SIGN_TYPE takes "Sign_with_release_certs" or "Sign_with_debug_certs"
# APK_SOURCE_FILE takes the full path of the apk
# SIGNED_APK_PATH takes the folder to which it needs to be copied 


DownloadCert()
{
	cd ${apk_signer_path}/CertUtils_new_debug
        _CertUtils_path=`pwd`
        export PATH="$PATH:${_CertUtils_path}"
        chmod 755 SPCertSvc
  
	mkdir -p $release_certi_path
        SPCertSvc -u ${username} -p ${password} -o $release_certi_path/platform -c "Indigo2-Android Platform"
     	SPCertSvc -u ${username} -p ${password} -o $release_certi_path/platform -c "Indigo2-Android Platform" -k

        mv $release_certi_path/platform.pk8 $release_certi_path/platform.pem
	openssl pkcs8 -in $release_certi_path/platform.pem -topk8 -outform DER -out $release_certi_path/platform.pk8 -nocrypt
	cd $apk_signer_path	
}

main()
{
	if [ ${SIGN_TYPE} == "Sign_with_release_certs" ];then
		DownloadCert
	fi

	rm -f *.apk
	# cp $APK_SOURCE_PATH/*.$RELEASE_TYPE.apk $apk_signer_path/.
	echo "APK_SOURCE_FILE : ${APK_SOURCE_FILE}"
	echo "apk_signer_path : ${apk_signer_path}"
	cp $APK_SOURCE_FILE $apk_signer_path/.
	apk_name=`ls *.apk|awk -F".apk" '{print $1}'`

	if [ ${SIGN_TYPE} == "Sign_with_release_certs" ];then
		echo "Signing with release certs"
		$tool_path/apksigner sign --key $release_certi_path/platform.pk8 --cert $release_certi_path/platform.x509.pem --out $apk_signer_path/${apk_name}_releasesigned.apk $apk_name.apk
		rm -rf $release_certi_path
	elif [ ${SIGN_TYPE} == "Sign_with_debug_certs" ];then
		echo "Signing with debug certs"
		$tool_path/apksigner sign --key $debug_certi_path/platform.pk8 --cert $debug_certi_path/platform.x509.pem --out $apk_signer_path/${apk_name}_debugsigned.apk $apk_name.apk
	fi
	
	cp $apk_signer_path/*.*signed.apk $SIGNED_APK_PATH/.
	
}

main $@
