package com.loan.iaexplore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs

// ─── Theme Colors ───────────────────────────────────────────────────────────
val DarkBg = Color(0xFF0D0D0D)
val SurfaceCardLight = Color(0xFF242424)
val AccentRed = Color(0xFFE53935)
val TextWhite = Color(0xFFF5F5F5)
val TextGray = Color(0xFF9E9E9E)
val TextMuted = Color(0xFF6B6B6B)
val ChipBg = Color(0xFF2A2A2A)
val ChipSelected = Color(0xFFE53935)
val SearchBg = Color(0xFF1E1E1E)
val DividerColor = Color(0xFF2A2A2A)

// ─── Data Models ────────────────────────────────────────────────────────────
data class CategoryItem(val icon: ImageVector, val label: String)
data class PromoCard(val title: String, val subtitle: String, val gradient: List<Color>)
data class TabItem(val label: String)
data class FeatureCard(
    val title: String,
    val description: String,
    val buttonText: String,
    val gradient: List<Color>
)
data class OfferCard(
    val brand: String,
    val description: String,
    val iconVector: ImageVector,
    val iconBgColor: Color,
    val hasNewBadge: Boolean = false
)
data class EventItem(
    val title: String,
    val subtitle: String,
    val date: String
)

// ─── Sample Data ────────────────────────────────────────────────────────────
val categories = listOf(
    CategoryItem(Icons.Outlined.AccountBalance, "Accounts"),
    CategoryItem(Icons.Outlined.CreditCard, "Cards"),
    CategoryItem(Icons.Outlined.Payments, "Financing"),
    CategoryItem(Icons.Outlined.Savings, "Saving"),
    CategoryItem(Icons.Outlined.CurrencyExchange, "Transfer"),
)

val promoCards = listOf(
    PromoCard(
        "Don't wait for payday. Access your salary early",
        "Get up to 50% of your salary before payday",
        listOf(Color(0xFFE53935), Color(0xFFAD1457))
    ),
    PromoCard(
        "One Card, 13 Currencies, Worldwide",
        "Use your card globally without extra fees",
        listOf(Color(0xFF1565C0), Color(0xFF0D47A1))
    ),
    PromoCard(
        "Earn miles every time you spend",
        "Collect rewards on all your purchases",
        listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
    ),
)

val tabs = listOf(
    TabItem("Go global"),
    TabItem("Earn rewards"),
    TabItem("Card & cards"),
    TabItem("More deals"),
    TabItem("Meet a cashier"),
    TabItem("Cashback"),
)

val tabFeatures: Map<Int, List<FeatureCard>> = mapOf(
    0 to listOf(
        FeatureCard("Earn Miles Every Time You Spend", "Get 1x miles for every dollar spent on everyday purchases using your miles card.", "Learn More", listOf(Color(0xFF37474F), Color(0xFF263238))),
        FeatureCard("One Card, 13 Currencies, Worldwide", "Carry one prepaid card, shop in 13 currencies worldwide. No hidden fees, no surprises.", "Know More", listOf(Color(0xFF1A237E), Color(0xFF0D1B4A))),
        FeatureCard("Foreign Currency Account", "Open a foreign currency account and manage your money in multiple currencies with ease.", "Know More", listOf(Color(0xFF4E342E), Color(0xFF3E2723))),
        FeatureCard("Meet Nomo, the international bank that fits in your pocket.", "Bank internationally with zero hassle, open your Nomo account today.", "Know More", listOf(Color(0xFF212121), Color(0xFF111111))),
        FeatureCard("Earn Miles Every Time You Spend", "Get 1x miles for every dollar spent on everyday purchases using your miles card.", "Learn More", listOf(Color(0xFF37474F), Color(0xFF263238))),
        FeatureCard("One Card, 13 Currencies, Worldwide", "Carry one prepaid card, shop in 13 currencies worldwide. No hidden fees, no surprises.", "Know More", listOf(Color(0xFF1A237E), Color(0xFF0D1B4A))),
        FeatureCard("Foreign Currency Account", "Open a foreign currency account and manage your money in multiple currencies with ease.", "Know More", listOf(Color(0xFF4E342E), Color(0xFF3E2723))),
        FeatureCard("Meet Nomo, the international bank that fits in your pocket.", "Bank internationally with zero hassle, open your Nomo account today.", "Know More", listOf(Color(0xFF212121), Color(0xFF111111))),
    ),
    1 to listOf(
        FeatureCard("Double Miles Weekend", "Earn 2x miles on all weekend purchases. Valid on dining and entertainment.", "Activate Now", listOf(Color(0xFF880E4F), Color(0xFF4A0028))),
        FeatureCard("Refer & Earn 5,000 Miles", "Invite friends to join and get rewarded with bonus miles for every successful referral.", "Refer Now", listOf(Color(0xFF4A148C), Color(0xFF2A0054))),
        FeatureCard("Miles Booster Pack", "Purchase a miles booster to multiply your earnings for the next 30 days.", "Get Booster", listOf(Color(0xFF1B5E20), Color(0xFF0A3A10))),
    ),
    2 to listOf(
        FeatureCard("Virtual Card Instantly", "Get a virtual card in seconds. Shop online securely without waiting for a physical card.", "Get Card", listOf(Color(0xFF0D47A1), Color(0xFF062A60))),
        FeatureCard("Freeze & Unfreeze Card", "Lost your card? Freeze it instantly from the app and unfreeze when found.", "Manage Cards", listOf(Color(0xFF37474F), Color(0xFF1C2C33))),
    ),
    3 to listOf(
        FeatureCard("Nike Store – 20% Off", "Exclusive deal for cardholders. Get 20% off at all Nike stores this month.", "View Offer", listOf(Color(0xFFBF360C), Color(0xFF7A1F00))),
        FeatureCard("Dining Cashback 15%", "Enjoy 15% cashback at partner restaurants when you pay with your card.", "Explore Restaurants", listOf(Color(0xFF6A1B9A), Color(0xFF3D0F5A))),
    ),
    4 to listOf(
        FeatureCard("Visit a Branch Near You", "Find the nearest branch and book an appointment with a cashier.", "Find Branch", listOf(Color(0xFF004D40), Color(0xFF002920))),
    ),
    5 to listOf(
        FeatureCard("Cashback on Groceries", "Get up to 10% cashback on grocery purchases at partner supermarkets.", "See Partners", listOf(Color(0xFFE65100), Color(0xFF993500))),
        FeatureCard("Monthly Cashback Summary", "Track all your cashback earnings and see how much you've saved this month.", "View Summary", listOf(Color(0xFF1A237E), Color(0xFF0D1250))),
    ),
)

val offers = listOf(
    OfferCard("Nike Store", "Get up to 20% off on all Nike products. Valid for all cardholders.", Icons.Outlined.ShoppingBag, Color(0xFF212121), true),
    OfferCard("Puma Outlet", "Exclusive 15% discount at Puma outlet stores this weekend.", Icons.Outlined.LocalOffer, Color(0xFF1B5E20), false),
    OfferCard("Starbucks", "Buy 1 Get 1 free on all beverages every Friday with your card.", Icons.Outlined.LocalCafe, Color(0xFF004D40), true),
    OfferCard("Amazon", "5% cashback on all Amazon purchases above \$50.", Icons.Outlined.ShoppingCart, Color(0xFFBF360C), false),
)

val events = listOf(
    EventItem("Boubyan E-league", "Register now to participate in Boubyan E-league tournament", "15 January 2026"),
    EventItem("Coffee Harvest by Boubyan", "Join us for a special coffee tasting event at our main branch", "February 2026"),
    EventItem("Boubyan Coffee Festival", "Join us at Boubyan's coffee festivals across the country", "March 2026"),
)

// ─── MainActivity ───────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = DarkBg,
                    surface = Color(0xFF1A1A1A),
                    onBackground = TextWhite,
                    onSurface = TextWhite,
                    primary = AccentRed,
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = DarkBg) {
                    ExploreScreen()
                }
            }
        }
    }
}

// ─── Explore Screen ─────────────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExploreScreen() {
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val tabsListState = rememberLazyListState()
    val listState = rememberLazyListState()

    // ── Track each page's content height in px ──
    val pageHeights = remember { mutableStateMapOf<Int, Int>() }

    // ── Interpolate pager height between current and target page during swipe ──
    val pagerHeightDp: Dp by remember {
        derivedStateOf {
            val currentPage = pagerState.currentPage
            val offset = pagerState.currentPageOffsetFraction
            val currentH = pageHeights[currentPage] ?: 0

            if (currentH == 0) return@derivedStateOf Dp.Unspecified

            val targetPage = if (offset > 0) {
                (currentPage + 1).coerceAtMost(tabs.size - 1)
            } else {
                (currentPage - 1).coerceAtLeast(0)
            }
            val targetH = pageHeights[targetPage] ?: currentH
            val fraction = abs(offset)
            val interpolated = currentH + (targetH - currentH) * fraction

            with(density) { interpolated.toInt().toDp() }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        val index = pagerState.currentPage

        val itemInfo = tabsListState.layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == index }
        val viewportWidth = tabsListState.layoutInfo.viewportEndOffset

        if (itemInfo != null) {
            val offset = itemInfo.offset - (viewportWidth / 2 - itemInfo.size / 2)
            tabsListState.animateScrollBy(offset.toFloat())
        } else {
            tabsListState.animateScrollToItem(index)
        }

        val tabsIndex = listState.layoutInfo.visibleItemsInfo
            .firstOrNull { it.key == "tabs" }?.index
        val firstVisible = listState.firstVisibleItemIndex

        if (tabsIndex != null && firstVisible > tabsIndex) {
            listState.scrollToItem(tabsIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        item(key = "title") {
            Text(
                text = "Explore",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        item(key = "search") { SearchBar() }

        item(key = "categories") {
            Spacer(modifier = Modifier.height(16.dp))
            CategoriesRow()
        }

        item(key = "foryou") {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "For you")
            Spacer(modifier = Modifier.height(12.dp))
            PromoCardsRow()
            Spacer(modifier = Modifier.height(20.dp))
        }

        stickyHeader(key = "tabs") {
            Column(modifier = Modifier.background(DarkBg)) {
                ScrollableTabsRow(
                    selectedIndex = pagerState.currentPage,
                    listState = tabsListState,
                    onTabSelected = { index ->
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
            }
        }

        // ── HorizontalPager with dynamic height ──
        item(key = "pager") {
            val heightModifier = if (pagerHeightDp != Dp.Unspecified) {
                Modifier.height(pagerHeightDp)
            } else {
                Modifier.wrapContentHeight()
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(heightModifier),
                beyondViewportPageCount = tabs.size - 1,
                verticalAlignment = Alignment.Top,
            ) { page ->
                val features = tabFeatures[page] ?: emptyList()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { size ->
                            val current = pageHeights[page]
                            if (current == null || size.height > current) {
                                pageHeights[page] = size.height
                            }
                        }
                        .padding(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    features.forEach { feature ->
                        FeatureCardItem(feature)
                    }
                }
            }
        }

        // ─── Sections below the pager (visible on ALL tabs) ─────────────

        item(key = "offers_header") {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Discounts & Offers", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                Text("View all", fontSize = 13.sp, color = AccentRed, modifier = Modifier.clickable { })
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item(key = "offers_row") { OffersRow() }

        item(key = "events_header") {
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Happening at Boubyan", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                Text("See all", fontSize = 13.sp, color = AccentRed, modifier = Modifier.clickable { })
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(events.size, key = { "event_$it" }) { index ->
            EventListItem(events[index])
            if (index < events.size - 1) {
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 20.dp))
            }
        }

        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ─── Search Bar ─────────────────────────────────────────────────────────────
@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SearchBg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Search, contentDescription = "Search", tint = TextMuted, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text("Search products, offers, campaigns...", color = TextMuted, fontSize = 14.sp)
    }
}

// ─── Category Row ───────────────────────────────────────────────────────────
@Composable
fun CategoriesRow() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(categories) { CategoryIcon(it) }
    }
}

@Composable
fun CategoryIcon(item: CategoryItem) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(64.dp)) {
        Box(
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceCardLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(item.icon, contentDescription = item.label, tint = TextWhite, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(item.label, color = TextGray, fontSize = 12.sp, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// ─── Section Header ─────────────────────────────────────────────────────────
@Composable
fun SectionHeader(title: String) {
    Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextWhite, modifier = Modifier.padding(horizontal = 20.dp))
}

// ─── Promo Cards Row ────────────────────────────────────────────────────────
@Composable
fun PromoCardsRow() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(promoCards) { PromoCardItem(it) }
    }
}

@Composable
fun PromoCardItem(card: PromoCard) {
    Box(
        modifier = Modifier.width(280.dp).height(160.dp).clip(RoundedCornerShape(16.dp))
            .background(brush = Brush.horizontalGradient(card.gradient)).padding(20.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
            Text(card.title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(card.subtitle, color = TextWhite.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

// ─── Offers Row ─────────────────────────────────────────────────────────────
@Composable
fun OffersRow() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(offers) { OfferCardItem(it) }
    }
}

@Composable
fun OfferCardItem(offer: OfferCard) {
    Box(modifier = Modifier.width(200.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceCardLight)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(offer.iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(offer.iconVector, contentDescription = offer.brand, tint = TextWhite, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(offer.brand, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                if (offer.hasNewBadge) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(AccentRed).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("New", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(offer.description, color = TextGray, fontSize = 12.sp, lineHeight = 16.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}

// ─── Event List Item ────────────────────────────────────────────────────────
@Composable
fun EventListItem(event: EventItem) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceCardLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = AccentRed, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(event.subtitle, color = TextGray, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(event.date, color = TextMuted, fontSize = 11.sp)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp).align(Alignment.CenterVertically))
    }
}

// ─── Scrollable Tabs ────────────────────────────────────────────────────────
@Composable
fun ScrollableTabsRow(selectedIndex: Int, listState: LazyListState, onTabSelected: (Int) -> Unit) {
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        itemsIndexed(tabs) { index, tab ->
            TabChip(tab.label, index == selectedIndex) { onTabSelected(index) }
        }
    }
}

@Composable
fun TabChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(if (isSelected) ChipSelected else ChipBg, tween(250), label = "chipBg")
    val textColor by animateColorAsState(if (isSelected) Color.White else TextGray, tween(250), label = "chipText")
    Box(
        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(bgColor).clickable { onClick() }.padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(label, color = textColor, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

// ─── Feature Card ───────────────────────────────────────────────────────────
@Composable
fun FeatureCardItem(card: FeatureCard) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).clip(RoundedCornerShape(20.dp))
            .background(brush = Brush.verticalGradient(card.gradient))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text(card.title, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(card.description, color = TextWhite.copy(alpha = 0.7f), fontSize = 14.sp, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(card.buttonText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}