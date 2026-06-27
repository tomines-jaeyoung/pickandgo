// ============================================================
// Pick & Go - 가구 처리 미니게임
// ============================================================
const PG = (() => {
  'use strict';

  // ── 상수 ──────────────────────────────────────────────────
  const FURNITURE = [
    { id:'sofa',     emoji:'🛋️', name:'소파'    },
    { id:'bed',      emoji:'🛏️', name:'침대'    },
    { id:'tv',       emoji:'📺',  name:'TV'     },
    { id:'chair',    emoji:'🪑',  name:'의자'   },
    { id:'desk',     emoji:'🗄️', name:'책상'   },
    { id:'wardrobe', emoji:'🚪',  name:'옷장'   },
    { id:'table',    emoji:'🪵',  name:'테이블'  },
  ];
  const SERVICES = ['organize', 'storage', 'sell'];
  const SVC_LABEL = { organize:'정리하기', storage:'맡겨두기', sell:'판매하기' };

  // 레벨업 비용 (목표레벨 기준)
  const LV_COST = {
    organize: { 2:15, 3:40 },
    storage:  { 2:15, 3:40 },
    sell:     { 2:15, 3:40 },
  };

  // ── 저장/로드 ─────────────────────────────────────────────
  function loadSaved() {
    return {
      stars: 0,
      levels: { organize: 1, storage: 1, sell: 1 },
      containers: [{ id: 0, level: 1, items: [] }],
    };
  }
  function save() {
    // 새로고침 시 초기화 - 저장 안 함
  }

  // ── 게임 상태 ─────────────────────────────────────────────
  const saved = loadSaved();
  const S = {
    ...saved,
    missions:    [],
    beltItems:   [],
    _id:         1,
    _lastMissionTime: Date.now(),
  };

  // ── DOM 참조 ──────────────────────────────────────────────
  let beltEl, missionRowEl, starsEl, wrapEl;

  // ── 초기화 ────────────────────────────────────────────────
  document.addEventListener('DOMContentLoaded', () => {
    beltEl       = document.getElementById('game-belt');
    missionRowEl = document.getElementById('mission-row');
    starsEl      = document.getElementById('g-stars');
    wrapEl       = document.getElementById('game-wrap');

    updateStarsUI();
    updateLevelUI();
    renderContainers();
    renderQueue();

    // 게임 존 카드 클릭 → 페이지 이동
    // 정리하기 / 판매하기 → /organize, 맡겨두기 → /storage
    const zoneNav = { 'zone-organize':'/organize', 'zone-storage':'/storage', 'zone-sell':'/organize' };
    Object.entries(zoneNav).forEach(([id, url]) => {
      const el = document.getElementById(id);
      if (!el) return;
      el.style.cursor = 'pointer';
      el.addEventListener('click', e => {
        // + 버튼, 컨테이너 레벨업 버튼 클릭은 무시
        if (e.target.closest('.lv-up-btn') || e.target.closest('.c-lvup-btn')) return;
        window.location.href = url;
      });
    });

    // 초기 미션 2개
    addMission();
    addMission();

    setInterval(gameLoop,        50);   // 벨트 이동
    setInterval(checkMissions,  1000);  // 미션 수 체크
    setInterval(containerIncome,1000);  // 컨테이너 수익
  });

  // ── 게임 루프 ─────────────────────────────────────────────
  function gameLoop() {
    const SPEED = 0.22;
    const fallen = [];

    S.beltItems.forEach(item => {
      if (item.isDragging) return;
      item.pos += SPEED;
      if (item.el) item.el.style.left = item.pos + '%';
      if (item.pos >= 101) fallen.push(item);
    });

    fallen.forEach(item => {
      if (item.el) { item.el.classList.add('fall-off'); }
      S.beltItems = S.beltItems.filter(i => i.id !== item.id);
      setTimeout(() => { if (item.el) item.el.remove(); }, 400);
      // 해당 미션이 아직 살아있으면 3초 후 재등장
      const alive = S.missions.some(m => m.furniture.id === item.furniture.id);
      if (alive) setTimeout(() => spawnBeltItem(item.furniture), 3000);
    });
  }

  // ── 미션 시스템 ───────────────────────────────────────────
  function addMission() {
    if (S.missions.length >= 3) return;
    const furniture = FURNITURE[Math.floor(Math.random() * FURNITURE.length)];

    // 맡겨두기는 빈 컨테이너 공간이 있을 때만 미션 부여 (실시간 체크)
    let available = [...SERVICES];
    const hasStorageSpace = S.containers.some(c => c.items.length < c.level);
    if (!hasStorageSpace) {
      available = available.filter(s => s !== 'storage');
    }
    if (!available.length) available = ['organize', 'sell'];

    const service = available[Math.floor(Math.random() * available.length)];
    const id = S._id++;
    S.missions.push({ id, furniture, service });
    S._lastMissionTime = Date.now();
    renderMissions();
    spawnBeltItem(furniture);
  }

  function checkMissions() {
    if (S.missions.length < 2) addMission();
    if (S.missions.length < 3 && Date.now() - S._lastMissionTime > 10000) addMission();
  }

  function completeMission(id) {
    S.missions = S.missions.filter(m => m.id !== id);
    renderMissions();
    setTimeout(() => { if (S.missions.length < 2) addMission(); }, 1500);
  }

  function renderMissions() {
    if (!missionRowEl) return;

    // 완료돼서 사라진 미션 DOM 제거 (fade out)
    const rendered = missionRowEl.querySelectorAll('.mission-person');
    rendered.forEach(el => {
      const id = parseInt(el.dataset.missionId);
      if (!S.missions.find(m => m.id === id)) {
        el.classList.add('mission-leave');
        setTimeout(() => { if (el.parentNode) el.remove(); }, 400);
      }
    });

    // 새로 추가된 미션만 DOM에 추가 (기존 것은 건드리지 않음)
    S.missions.forEach(m => {
      if (missionRowEl.querySelector(`[data-mission-id="${m.id}"]`)) return;
      const el = document.createElement('div');
      el.className = 'mission-person';
      el.dataset.missionId = m.id;
      el.innerHTML = `
        <div class="speech-bubble">
          ${m.furniture.emoji} <strong>${m.furniture.name}</strong>을(를)<br>
          <em>${SVC_LABEL[m.service]}</em>해주세요!
        </div>
        <div class="person-walk">🚶</div>
      `;
      missionRowEl.appendChild(el);
    });
  }

  // ── 벨트 아이템 ───────────────────────────────────────────
  function spawnBeltItem(furniture) {
    if (!beltEl) return;

    // 벨트 위 다른 아이템과의 최소 간격 체크 (겹침 방지)
    let minPos = 100;
    S.beltItems.forEach(item => {
      if (item.pos < minPos) {
        minPos = item.pos;
      }
    });

    const MIN_GAP = 20; // 최소 안전 간격 (%)
    if (minPos < MIN_GAP) {
      // 1초당 4.4%씩 이동하므로 부족한 거리만큼 환산하여 자연스러운 지연 시간 산출
      const delay = Math.max(400, ((MIN_GAP - minPos) / 4.4) * 1000);
      setTimeout(() => spawnBeltItem(furniture), delay);
      return;
    }

    const id = S._id++;
    const el = document.createElement('div');
    el.className = 'belt-item-el';
    el.textContent = furniture.emoji;
    el.draggable = true;
    el.title = furniture.name;
    el.style.left = '-6%';
    el.dataset.itemId = id;

    el.addEventListener('dragstart', e => {
      e.dataTransfer.setData('pg_id', String(id));
      el.classList.add('is-dragging');
      const it = S.beltItems.find(i => i.id === id);
      if (it) it.isDragging = true;
    });
    el.addEventListener('dragend', () => {
      el.classList.remove('is-dragging');
      const it = S.beltItems.find(i => i.id === id);
      if (it) it.isDragging = false;
    });

    beltEl.appendChild(el);
    S.beltItems.push({ id, furniture, el, pos: -6, isDragging: false });
  }

  // ── 드롭 핸들러 ───────────────────────────────────────────
  function handleDrop(event, service) {
    event.preventDefault();
    event.currentTarget.classList.remove('drag-over');

    const itemId = parseInt(event.dataTransfer.getData('pg_id'));
    if (!itemId) return;
    const itemIdx = S.beltItems.findIndex(i => i.id === itemId);
    if (itemIdx === -1) return;
    const item = S.beltItems[itemIdx];

    // 미션 매칭
    const mIdx = S.missions.findIndex(
      m => m.furniture.id === item.furniture.id && m.service === service
    );
    if (mIdx === -1) { bounceBack(item); return; }

    // 맡겨두기: 용량 체크
    if (service === 'storage') {
      const c = S.containers.find(c => c.items.length < c.level);
      if (!c) { bounceBack(item, '컨테이너가 꽉 찼습니다! 레벨업하세요.'); return; }
    }

    // 성공 처리
    if (item.el) item.el.remove();
    S.beltItems.splice(itemIdx, 1);

    doReaction(service, item.furniture, event.currentTarget);
    completeMission(S.missions[mIdx].id);
  }

  function bounceBack(item, msg) {
    playBuzz();
    if (msg) showMsg(msg);
    if (!item.el) return;
    item.isDragging = false;
    item.pos = 0;
    item.el.style.left = '0%';
    item.el.classList.add('bounce-back');
    setTimeout(() => { if (item.el) item.el.classList.remove('bounce-back'); }, 700);
  }

  // ── 액터 리액션 ───────────────────────────────────────────
  function doReaction(service, furniture, zoneEl) {
    if (service === 'organize') {
      const n = S.levels.organize;
      addStars(n, zoneEl);
      animateTruck(furniture);

    } else if (service === 'storage') {
      const c = S.containers.find(c => c.items.length < c.level);
      c.items.push(furniture.id);
      save();
      renderContainers();
      animateContainer();

    } else if (service === 'sell') {
      const n = S.levels.sell;
      addStars(n, zoneEl);
      animateBuyer(furniture);
    }
  }

  function animateTruck(furniture) {
    const el = document.getElementById('g-truck');
    if (!el) return;
    const cargo = document.createElement('span');
    cargo.className = 'truck-cargo';
    cargo.textContent = furniture.emoji;
    el.appendChild(cargo);
    el.classList.add('truck-load');
    setTimeout(() => {
      el.classList.remove('truck-load');
      el.classList.add('truck-go-left');
      setTimeout(() => {
        el.classList.remove('truck-go-left');
        cargo.remove();
      }, 1400);
    }, 300);
  }

  function animateContainer() {
    const units = document.querySelectorAll('.container-unit');
    if (!units.length) return;
    const last = units[units.length - 1];
    last.classList.add('c-squish');
    setTimeout(() => last.classList.remove('c-squish'), 500);
  }

  function animateBuyer(furniture) {
    const qEl = document.getElementById('sell-queue');
    if (!qEl) return;
    const persons = qEl.querySelectorAll('.q-person');
    if (!persons.length) return;
    const first = persons[persons.length - 1];
    first.textContent = furniture.emoji + '🏃';
    first.classList.add('buyer-go');
    setTimeout(() => {
      first.remove();
      const np = document.createElement('div');
      np.className = 'q-person q-new';
      np.textContent = '🧍';
      qEl.prepend(np);
      setTimeout(() => np.classList.remove('q-new'), 500);
    }, 900);
  }

  // ── 별 시스템 ─────────────────────────────────────────────
  function addStars(n, refEl) {
    S.stars += n;
    updateStarsUI();
    save();
    showStarPop(n, refEl);
  }

  function showStarPop(n, refEl) {
    if (!wrapEl) return;
    const pop = document.createElement('div');
    pop.className = 'star-pop';
    pop.textContent = '+' + n + '⭐';
    let x = 300, y = 150;
    if (refEl) {
      const r  = refEl.getBoundingClientRect();
      const gr = wrapEl.getBoundingClientRect();
      x = r.left - gr.left + r.width / 2;
      y = r.top  - gr.top  + r.height / 4;
    }
    pop.style.left = x + 'px';
    pop.style.top  = y + 'px';
    wrapEl.appendChild(pop);
    setTimeout(() => pop.remove(), 1100);
  }

  function updateStarsUI() {
    if (starsEl) starsEl.textContent = S.stars.toLocaleString();
  }

  // ── 레벨업 ────────────────────────────────────────────────
  function tryLevelUp(service) {
    const cur = S.levels[service];
    if (cur >= 3) { alert('이미 최고 레벨(Lv.3)입니다!'); return; }
    const next = cur + 1;
    const cost = LV_COST[service][next];
    if (S.stars < cost) {
      alert(`별이 부족합니다!\n필요: ${cost}⭐  현재: ${S.stars}⭐`); return;
    }
    const desc = service === 'organize'
      ? `정리하기 배달 별 ${cur}개 → ${next}개`
      : service === 'storage'
        ? `컨테이너 1대 추가 (총 ${next}대)`
        : `판매 별 ${cur}개 → ${next}개`;

    if (!confirm(`레벨업하시겠습니까?\n\n${SVC_LABEL[service]} Lv.${cur} → Lv.${next}\n${desc}\n\n비용: ${cost}⭐`)) return;

    S.stars -= cost;
    S.levels[service] = next;
    if (service === 'storage') {
      S.containers.push({ id: S._id++, level:1, items:[] });
      renderContainers();
    }
    updateStarsUI();
    updateLevelUI();
    save();
  }

  function tryContainerLevelUp(idx) {
    const c = S.containers[idx];
    if (!c) return;
    const cost = c.level * 10;
    if (S.stars < cost) {
      alert(`별이 부족합니다!\n필요: ${cost}⭐  현재: ${S.stars}⭐`); return;
    }
    if (!confirm(
      `컨테이너 레벨업하시겠습니까?\nLv.${c.level} → Lv.${c.level+1}\n용량 ${c.level}개 → ${c.level+1}개\n비용: ${cost}⭐`
    )) return;
    S.stars -= cost;
    c.level++;
    updateStarsUI();
    renderContainers();
    save();
  }

  function updateLevelUI() {
    SERVICES.forEach(s => {
      const el = document.getElementById('lv-' + s);
      if (el) el.textContent = 'Lv.' + S.levels[s];
    });
  }

  // ── 컨테이너 렌더 ─────────────────────────────────────────
  function renderContainers() {
    const body = document.getElementById('storage-body');
    if (!body) return;
    body.innerHTML = '';
    S.containers.forEach((c, i) => {
      const icons = c.items.map(fid => {
        const f = FURNITURE.find(x => x.id === fid);
        return f ? f.emoji : '📦';
      }).join('');
      const div = document.createElement('div');
      div.className = 'container-unit';
      div.innerHTML = `
        <div class="c-head">🗃️ <b>Lv.${c.level}</b>
          <span class="c-cap">${c.items.length}/${c.level}</span>
          <button class="c-lvup-btn" onclick="PG.tryContainerLevelUp(${i})">
            +Lv <small>(${c.level*10}⭐)</small>
          </button>
        </div>
        <div class="c-items">${icons || '<span class="c-empty">비어있음</span>'}</div>
      `;
      body.appendChild(div);
    });
  }

  // ── 판매 대기줄 렌더 ──────────────────────────────────────
  function renderQueue() {
    const el = document.getElementById('sell-queue');
    if (!el) return;
    el.innerHTML = '';
    for (let i = 0; i < 8; i++) {
      const p = document.createElement('div');
      p.className = 'q-person';
      p.textContent = '🧍';
      el.appendChild(p);
    }
  }

  // ── 컨테이너 초당 수익 ────────────────────────────────────
  function containerIncome() {
    let total = 0;
    S.containers.forEach(c => { total += c.items.length; });
    if (total > 0) {
      S.stars += total;
      updateStarsUI();
      save();
      showStarPop(total, document.getElementById('zone-storage'));
    }
  }

  // ── 효과음 (Web Audio API) ────────────────────────────────
  function playBuzz() {
    try {
      const ctx  = new (window.AudioContext || window.webkitAudioContext)();
      const osc  = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain); gain.connect(ctx.destination);
      osc.type = 'sawtooth';
      osc.frequency.setValueAtTime(160, ctx.currentTime);
      gain.gain.setValueAtTime(0.25, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.28);
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.28);
    } catch(e) {}
  }

  // ── 유틸 ─────────────────────────────────────────────────
  function showMsg(text) {
    if (!wrapEl) return;
    const el = document.createElement('div');
    el.className = 'game-msg';
    el.textContent = text;
    wrapEl.appendChild(el);
    setTimeout(() => el.remove(), 2000);
  }

  return { handleDrop, tryLevelUp, tryContainerLevelUp };
})();
