package cn.aksu.supervision.enterprise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegulatoryInfoDTO {

    /** 企业基本信息 */
    private EnterpriseDTO enterprise;

    /** 属地监管单位名称 */
    private String regulatoryUnitName;

    /** 属地监管单位地址 */
    private String regulatoryUnitAddress;

    /** 属地监管单位电话 */
    private String regulatoryUnitPhone;

    /** 执法人员列表 */
    private List<EnforcerDTO> enforcers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnforcerDTO {
        /** 姓名 */
        private String name;

        /** 电话 */
        private String phone;

        /** 照片URL */
        private String photoUrl;

        /** 所属科室 */
        private String department;

        /** 执法证号 */
        private String lawEnforcementNo;
    }
}
