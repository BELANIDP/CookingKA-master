#!/bin/bash -x

_cicd_path=${WORKSPACE}/cicd
bms_input_folder=${_cicd_path}/sampleapps_bms_input/

upload_sample_apk_to_bms()
{
	if [[ $# -ne 5 ]]; then
		echo "Illegal number of parameters" >&2
		exit 2
	fi

	release_type=$1
	app=$2
	sign_type=$3
	bms_out_folder=$4
	sample_app_name=$5
	software_file_name=$(basename ${bms_out_folder}/*${app}*.apk)
	soft_ver=${NEW_TAG}
	soft_ver="${soft_ver:1}"

	# BMS Project Name convention
	# Indigo2 Rebel CookingSample Whirlpool Debug Unsigned
	appname=$(echo -n "${app^}")
	rel_type=$(echo -n "${release_type^}")
	#proj_name=Indigo2_Rebel_CookingSample_${appname}_${rel_type}_${sign_type}
	proj_name=Indigo2_HMI_${sample_app_name}_${appname}_${rel_type}_${sign_type}
	
	if [ -z "${BUILD_USER_ID}" ]; then
		BUILD_USER_ID="jakkus"
	else
		echo "BUILDER USER ID is set to '${BUILD_USER_ID}'";
	fi

# Printing required variables
	echo "proj_name=${proj_name}"
        echo "soft_ver=${soft_ver}"
        echo "BUILD_URL=${BUILD_URL}"
        echo "BUILD_USER_ID=${BUILD_USER_ID}"
        echo "software_file_name=${software_file_name}"
        echo "bms_input_folder=${bms_input_folder}"
        echo "bms_out_folder=${bms_out_folder}"
	ls -ltr ${bms_input_folder}
	ls -ltr ${bms_out_folder}

        if [ ! -f ${bms_out_folder}/${software_file_name} ]; then
                echo "APK file is not present. exiting"
                exit 1
        fi

	
	# BMS executable path on the Jenkins Servers
        bms_tool_path=/data/maven/ltibppp/bms

        # Checking if BMS executable exists or not
        cd ${bms_tool_path}
        if [ ! -f ./BMS ]; then
                echo "BMS executable not available. exiting"
                exit 1
        fi 

	#Uploading image to BMS
        old_cwd=$(pwd)
        cd ${bms_tool_path}

        ./BMS /p ProjectName:${proj_name} /p SoftwareVersion:${soft_ver} /p BuildResultId:${BUILD_URL} /p BuildRequesterUserId:${BUILD_USER_ID} -p SoftwareFileName:${software_file_name}  /o ${bms_out_folder} /i ${bms_input_folder}

        # ./BMS /p ProjectName:${proj_name} /p SoftwareVersion:${soft_ver} /p BuildResultId:${BUILD_URL} /p BuildRequesterUserId:${BUILD_USER_ID} -p SoftwareFileName:${software_file_name}  /o ${bms_out_folder} /i ${bms_input_folder}

        if [ "$?" = "0" ]; then
                echo "APK image is successfully uploaded to BMS"
                cd ${old_cwd}
        else
                echo "APK image is not uploaded to BMS. exiting"
                cd ${old_cwd}
                exit 1
        fi
}

upload_sample_apk_to_bms $@
