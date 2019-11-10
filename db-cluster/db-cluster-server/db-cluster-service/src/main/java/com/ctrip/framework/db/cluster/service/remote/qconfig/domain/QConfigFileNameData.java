package com.ctrip.framework.db.cluster.service.remote.qconfig.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by @author zhuYongMing on 2019/11/7.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QConfigFileNameData {

    private List<String> normal;
}
