package com.jonathanchiou.organizer.api

import com.jonathanchiou.organizer.api.model.*
import com.jonathanchiou.organizer.main.ButtonModel
import com.jonathanchiou.organizer.main.MainFeedModel
import com.jonathanchiou.organizer.main.TitleModel
import com.jonathanchiou.organizer.scheduler.ClientEvent
import io.reactivex.Observable
import io.reactivex.functions.Function3
import retrofit2.Response
import java.util.concurrent.TimeUnit

class OrganizerClient(val organizerService: OrganizerService) {

    fun connect(googleIdToken: String): Observable<ApiUIModel<Token>> {
        return organizerService
            .connect(googleIdToken)
            .toApiUIModelStream()
    }

    fun getNotification(): Observable<ApiUIModel<Notification>> {
        return Observable.just(
            Response.success(
                Notification(
                    title = "Respond to events",
                    text = "You've been invited to some " +
                        "events. Please respond to them " +
                        "as soon as possible!",
                    actionType = "Respond now",
                    actionCount = 3)))
            .delay(500, TimeUnit.MILLISECONDS)
            .toApiUIModelStream()
    }

    fun getUpcomingEvents(): Observable<ApiUIModel<List<EventBlurb>>> {
        return Observable.just(Response.success(createUpcomingEventBlurbs()))
            .delay(500, TimeUnit.MILLISECONDS)
            .toApiUIModelStream()
    }

    fun getPastEvents(): Observable<ApiUIModel<List<EventBlurb>>> {
        return Observable.just(Response.success(createPastEventBlurbs()))
            .delay(500, TimeUnit.MILLISECONDS)
            .toApiUIModelStream()
    }

    // TODO: Slightly better, but still kind of sucky.
    fun getMainFeed(): Observable<ApiUIModel<List<MainFeedModel>?>> {
        return Observable.zip(
            getNotification(),
            getUpcomingEvents(),
            getPastEvents(),
            Function3<
                ApiUIModel<Notification>,
                ApiUIModel<List<EventBlurb>>,
                ApiUIModel<List<EventBlurb>>,
                ApiUIModel<List<MainFeedModel>?>> {
                notificationUIModel, upcomingEventsUIModel, pastEventsUIModel ->
                val state = lowestState(notificationUIModel.state,
                                        upcomingEventsUIModel.state,
                                        pastEventsUIModel.state)
                val mainFeedModels = ArrayList<MainFeedModel>(3)
                notificationUIModel.model?.let {
                    mainFeedModels.add(it)
                }

                val buttonModel = ButtonModel("Show more")

                upcomingEventsUIModel.model?.let {
                    mainFeedModels.add(TitleModel("Upcoming Events"))
                    mainFeedModels.addAll(it)

                    if (it.size == EVENT_BLURB_MAIN_FEED_PAGE_SIZE) {
                        mainFeedModels.add(buttonModel)
                    }
                }

                pastEventsUIModel.model?.let {
                    mainFeedModels.add(TitleModel("Past Events"))
                    mainFeedModels.addAll(it)

                    if (it.size == EVENT_BLURB_MAIN_FEED_PAGE_SIZE) {
                        mainFeedModels.add(buttonModel)
                    }
                }

                return@Function3 ApiUIModel(state, mainFeedModels)
            })
    }

    fun getPlaces(input: String, location: String?): Observable<ApiUIModel<List<Place>>> {
        return organizerService
            .getPlaces(if (!input.isEmpty()) input else null, location)
            .toApiUIModelStream()
    }

    fun searchAccounts(groupId: Int, query: String?): Observable<ApiUIModel<List<Account>>> {
        return Observable.just(Response.success(createAccounts()))
            .delay(500, TimeUnit.MILLISECONDS)
            .toApiUIModelStream()
    }

    fun createEvent(groupId: Int, clientEvent: ClientEvent): Observable<Response<EventBlurb>> {
        return Observable.just(
            Response.success(
                EventBlurb(
                    id = 42,
                    title = "",
                    date = 0,
                    creator = createAccount())))
    }

    companion object {
        const val EVENT_BLURB_MAIN_FEED_PAGE_SIZE = 5
    }
}