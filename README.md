# Unlimited Call Log

An Xposed/LSposed module that disables the 500 entries limit in the Android's
call log provider, essentially allowing you to have an unlimited call history.

Note that this only disables the call log provider's limit. Your dialer may still
enforce some other limit.

## How it works

The call log provider deletes any entry above 500 on insertion of new entries,
as can be seen in the AOSP code [here](https://android.googlesource.com/platform/frameworks/base/+/cc7212ddf54bc9ec55d1f08db39833a7c3d91078/core/java/android/provider/CallLog.java#1985).

This module hooks the `delete` method of the actual provider in the `com.android.providers.contacts`
package ([here](https://android.googlesource.com/platform/packages/providers/ContactsProvider/+/a631f3aade06e25bd170c0a372f7617ec6ed2261/src/com/android/providers/contacts/CallLogProvider.java#693)),
and intercepts any calls to it that come from the `CallLog` code mentioned above by inspecting
the SQL `SELECT` statement and looking for the 500 entries limit pattern, in which case it simply
returns without actually executing the deletion (the reason for inspecting the `SELECT` statement
instead of blocking deletion wholesale, is that we still want to allow the user to delete single
entries, or bulk delete their entire call history if they so choose).

## License

This code is licensed under the MIT license. See [LICENSE](LICENSE) for details.