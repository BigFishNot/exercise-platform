package com.exercise.campus.service.impl;

import com.exercise.campus.component.RedisUtils;
import com.exercise.campus.entity.po.UserInfo;
import com.exercise.campus.entity.vo.BodyDataSummaryVO;
import com.exercise.campus.entity.vo.CheckInTodayVO;
import com.exercise.campus.entity.vo.WeightGoalVO;
import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.exception.BusinessException;
import com.exercise.campus.service.AiEncourageService;
import com.exercise.campus.service.BodyDataService;
import com.exercise.campus.service.ExerciseCheckInService;
import com.exercise.campus.service.UserInfoService;
import com.exercise.campus.service.WeightGoalService;
import com.exercise.campus.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * AI 鼓励 Service 实现
 * - 限频: Redis 计数 ai:encourage:{userId}:{yyyyMMdd} (TTL 到次日凌晨)
 * - 调用 LLM 占位 (catch 异常降级)
 * - 兜底: 静态鼓励语池
 */
@Slf4j
@Service
public class AiEncourageServiceImpl implements AiEncourageService {

    /** 每日最大次数 */
    private static final int DAILY_LIMIT = 3;

    /** Redis key 前缀 */
    private static final String REDIS_KEY_PREFIX = "ai:encourage:";
    /** TTL: 36 小时（确保覆盖到次日 0 点） */
    private static final long TTL_SECONDS = 36 * 60 * 60L;

    private static final Random RANDOM = new Random();

    /**
     * 静态鼓励语池 - 按上下文分组
     * 后续接入 LLM 时可作为 fallback
     */
    private static final List<String> POOL = List.of(
            // 趋势向下 / 体重下降
            "坚持就是胜利！每一小步都在向目标靠近，给自己点掌声 👏",
            "数字在往对的方向跑，你比自己想象中更稳 💪",
            "你已经连续打卡，节奏感很棒，继续保持 ✨",
            "今天的你和昨天比，又稳了一点，这就够了。",
            // 中性
            "运动不在于多猛，在于持续。慢慢来，比较快 🌿",
            "数据不是目的，你比数字更重要。先休息，明天继续。",
            "允许自己有休息日，恢复也是训练的一部分。",
            "累了就歇一歇，记得喝水，记得微笑 ☀️",
            // 趋势上升 / 体重上涨
            "波动是正常的，看长期趋势，不要被一天的数字吓到。",
            "今天的数据可能不理想，但习惯比单次结果重要。",
            "明天又是新的一天，调整饮食 + 继续动起来 ✊",
            // 鼓励 / 鸡汤
            "你已经不再是几个月前那个犹豫要不要开始的人了。",
            "每一次点开这个页面，都是在和未来的自己对话。",
            "小小的坚持，大大的变化。继续稳稳地走 🍀"
    );

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private BodyDataService bodyDataService;

    @Autowired
    private ExerciseCheckInService exerciseCheckInService;

    @Autowired
    private WeightGoalService weightGoalService;

    @Override
    public String generate(String userId) {
        // 1. 限频校验
        String key = buildKey(userId);
        Long used = redisUtils.incr(key, 1, TTL_SECONDS);
        if (used == null) {
            log.warn("[aiEncourage] Redis incr failed, fallback to allow");
        } else if (used > DAILY_LIMIT) {
            // 超额：把刚加的减回去（防误判重试）
            redisUtils.decr(key, 1);
            throw new BusinessException(ResponseCodeEnum.AI_ENCOURAGE_LIMIT_EXCEEDED);
        }

        // 2. 收集上下文
        Context ctx = buildContext(userId);

        // 3. 尝试调 LLM（占位 + 异常降级）
        String text = null;
        try {
            text = callLlm(ctx);
        } catch (Exception e) {
            log.warn("[aiEncourage] LLM call failed, fallback to static pool: {}", e.getMessage());
        }

        // 4. 兜底：选一条静态
        if (text == null || text.isEmpty()) {
            text = pickFromPool(ctx);
        }

        log.info("[aiEncourage:generate] userId={} used={}/{} pick={}",
                userId, used, DAILY_LIMIT, text.substring(0, Math.min(20, text.length())) + "...");
        return text;
    }

    /* ========== 私有 ========== */

    private String buildKey(String userId) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        fmt.setTimeZone(DateUtils.GMT8);
        return REDIS_KEY_PREFIX + userId + ":" + fmt.format(new Date());
    }

    private Context buildContext(String userId) {
        Context c = new Context();
        try {
            UserInfo u = userInfoService.getById(userId);
            c.nickName = u != null ? u.getNickName() : "运动达人";
        } catch (Exception ignored) {}
        try {
            BodyDataSummaryVO s = bodyDataService.getTrend(userId,
                    com.exercise.campus.entity.query.BodyDataTrendQuery.class.cast(
                            new com.exercise.campus.entity.query.BodyDataTrendQuery() {{
                                setRange("7d");
                            }})).summary();
            c.latestWeight = s == null ? null : s.getLatestWeight();
            c.weightChange7d = s == null ? null : s.getWeightChange7d();
        } catch (Exception ignored) {}
        try {
            CheckInTodayVO ck = exerciseCheckInService.getToday(userId);
            c.checkInStatus = ck == null ? null : ck.getStatus();
        } catch (Exception ignored) {}
        try {
            WeightGoalVO g = weightGoalService.getActive(userId);
            c.targetWeight = g == null ? null : g.getTargetWeight();
        } catch (Exception ignored) {}
        return c;
    }

    /**
     * 调用 LLM - 占位
     * 后续接真实 API 时替换此方法
     */
    private String callLlm(Context ctx) throws Exception {
        // 故意抛异常走静态池
        throw new UnsupportedOperationException("LLM not configured, fallback to static pool");
    }

    private String pickFromPool(Context ctx) {
        // 根据上下文从池中挑一条
        // 1 已打卡 + 体重下降 -> 选下降组的
        // 1 已打卡 + 体重上升 -> 选上升组
        // 0 未打卡 -> 选中性
        // null/无数据 -> 随机
        Integer ck = ctx.checkInStatus;
        BigDecimal change = ctx.weightChange7d;
        int[] candidates;
        if (ck != null && ck == 1) {
            // 已打卡
            if (change != null && change.compareTo(BigDecimal.ZERO) < 0) {
                candidates = new int[]{0, 1, 2, 3, 12, 13, 14};
            } else if (change != null && change.compareTo(BigDecimal.ZERO) > 0) {
                candidates = new int[]{8, 9, 10, 11, 12, 14};
            } else {
                candidates = new int[]{2, 3, 4, 5, 6, 7, 12, 13};
            }
        } else if (ck != null && ck == 2) {
            // 时长不足
            candidates = new int[]{4, 5, 6, 7, 8, 10};
        } else {
            // 未打卡
            candidates = new int[]{4, 5, 6, 7, 8, 9, 10, 11};
        }
        int idx = candidates[RANDOM.nextInt(candidates.length)];
        return POOL.get(idx);
    }

    /** 内部上下文载体 */
    private static class Context {
        String nickName;
        BigDecimal latestWeight;
        BigDecimal weightChange7d;
        Integer checkInStatus;     // 1 DONE / 2 INSUFFICIENT / 3 NOT_DONE
        BigDecimal targetWeight;
    }
}